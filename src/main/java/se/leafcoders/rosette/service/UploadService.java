package se.leafcoders.rosette.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import se.leafcoders.rosette.config.RosetteSettings;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.reference.UploadFileRefs;
import se.leafcoders.rosette.model.upload.UploadFile;
import se.leafcoders.rosette.model.upload.UploadRequest;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.QueryId;
import se.leafcoders.rosette.util.RosetteMpegAudioFileReader;

@Service
public class UploadService {

    static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    private final int THUMB_WIDTH_ICON = 300;
    private final int THUMB_HEIGHT_ICON = 300;

    @Autowired
    private RosetteSettings rosetteSettings;
    @Autowired
    protected MongoTemplate mongoTemplate;
	@Autowired
	private SecurityService security;
	@Autowired
	private UploadFolderService uploadFolderService;
    @Autowired
    private FileStorageService fileStorageService;

	public UploadFile create(final String folderId, UploadRequest upload, HttpServletResponse response) {
		response.setStatus(HttpStatus.CREATED.value());

		validateFolderExist(folderId);
		security.checkPermission(new PermissionValue(PermissionType.UPLOADS, PermissionAction.CREATE, folderId));
		checkMimeTypePermission(folderId, upload.getMimeType());

		if (getFileByName(folderId, upload.getFileName()) != null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.alreadyExists"));
		}
        byte[] fileData = upload.getFileData();

        UploadFile uploadFile = new UploadFile();
        uploadFile.setFolderId(folderId);
        uploadFile.setFileName(upload.getFileName());
        uploadFile.setMimeType(upload.getMimeType());
        uploadFile.setFileSize(fileData.length);

        // Set meta data
		if (upload.getMimeType().startsWith("image") && !setImageSizeMetadata(uploadFile, fileData)) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.image.invalidSize"));
		}
        if (upload.getMimeType().startsWith("audio")) {
            setAudioDurationMetadata(uploadFile, fileData, upload.getMimeType());
        }

		// Store file on disk
		boolean stored = fileStorageService.storeFile(folderId, null, upload.getFileName(), fileData);
		if (!stored) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.invalidFileContent"));
		}
		
		// Store meta data in database
		mongoTemplate.insert(uploadFile);
		response.setStatus(HttpStatus.CREATED.value());
		return withAbsolutePath(uploadFile);
	}

	public UploadFile read(final String uploadId, boolean checkPermissions) {
		UploadFile uploadFile = getFileById(uploadId);
		if (uploadFile != null) {
		    if (checkPermissions) {
		        security.checkPermission(new PermissionValue(PermissionType.UPLOADS, PermissionAction.READ, uploadFile.getFolderId(), uploadId));
		    }
	        return withAbsolutePath(uploadFile);
        } else {
			throw new NotFoundException("UploadFile", uploadId);
		}
	}
	
	public List<UploadFile> readAll(final String folderId) {
		validateFolderExist(folderId);

		List<UploadFile> filesInFolder = getFilesInFolder(folderId);
		List<UploadFile> uploads = new LinkedList<UploadFile>();
		if (filesInFolder != null) {
			for (UploadFile file : filesInFolder) {
				if (security.isPermitted(new PermissionValue(PermissionType.UPLOADS, PermissionAction.READ, folderId, file.getId().toString()))) {
					uploads.add(withAbsolutePath(file));
				}
			}
		}
		return uploads;
	}

	public void delete(final String folderId, final String uploadId, HttpServletResponse response) {
		validateFolderExist(folderId);
		security.checkPermission(new PermissionValue(PermissionType.UPLOADS, PermissionAction.DELETE, folderId, uploadId));
		security.checkNotReferenced(uploadId, UploadFile.class);

		if (deleteFileById(folderId, uploadId)) {
			response.setStatus(HttpStatus.OK.value());
		} else {
			throw new NotFoundException("UploadFile", uploadId);
		}
	}

	public boolean containsUploads(String folderId, UploadFileRefs uploads) {
		List<String> uploadIdsInFolder = getFileIdsInFolder(folderId);
		for (UploadFile upload : uploads) {
			if (!uploadIdsInFolder.contains(upload.getId())) {
				return false;
			}
		}
		return true;
	}

	public List<String> getFileIdsInFolder(String folderId) {
		List<UploadFile> filesInFolder = getFilesInFolder(folderId);
		List<String> uploadIds = new LinkedList<String>();
		if (filesInFolder != null) {
			for (UploadFile file : filesInFolder) {
				uploadIds.add(file.getId());
			}
		}
		return uploadIds;
	}

    public void streamAsset(final String folderId, final String fileNameOrId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        checkFolderPermission(folderId);
        UploadFile file = getFile(folderId, fileNameOrId);
        if (file != null) {
            fileStorageService.streamFile(null, file, request, response);
        } else {
            throw new NotFoundException("UploadFile", fileNameOrId);
        }
    }

    public void streamAssetThumbnail(final String folderId, final String fileNameOrId, final String thumbSize,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        checkFolderPermission(folderId);
        UploadFile file = getFile(folderId, fileNameOrId);
        if (file != null) {
            String fileName = file.getFileName();
            if (!fileStorageService.fileExist(file.getFolderId(), thumbSize, fileName)) {
                if (!fileStorageService.folderExist(file.getFolderId(), thumbSize)) {
                    if (!fileStorageService.createFolder(file.getFolderId(), thumbSize)) {
                        throw new SimpleValidationException(new ValidationError("uploadFile", "uploadFile.failedToCreateFolder"));                        
                    }
                }
                createThumbSize(folderId, fileName, thumbSize);
            }
            fileStorageService.streamFile(thumbSize, file, request, response);
        } else {
            throw new NotFoundException("UploadFile", fileNameOrId);
        }
    }

    private void checkFolderPermission(final String folderId) throws Exception {
        validateFolderExist(folderId);
        if (uploadFolderService.isPublic(folderId) == false) {
            security.checkPermission(
                    new PermissionValue(PermissionType.ASSETS, PermissionAction.READ, folderId),
                    new PermissionValue(PermissionType.UPLOADS, PermissionAction.READ, folderId));
        }
    }

	private void validateFolderExist(String folderId) {
		if (uploadFolderService.folderExist(folderId) == false) {
			throw new SimpleValidationException(new ValidationError("upload", "uploadFolder.dontExist"));
		}
	}
	
	private void checkMimeTypePermission(final String folderId, final String mimeType) {
		if (!uploadFolderService.isPermittedMimeType(folderId, mimeType)) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.mimeType.notAllowed"));
		}
	}

    private UploadFile createThumbSize(final String folderId, final String fileName, final String thumbSize) throws IOException {
        UploadFile file = getFile(folderId, fileName);
        if (file != null && file.getMimeType().startsWith("image")) {
            String orgFilePath = fileStorageService.absolutePath(folderId, fileName);
            String thumbFilePath = fileStorageService.absolutePath(folderId, thumbSize, fileName);
            Thumbnails.of(orgFilePath)
                .crop(Positions.CENTER)
                .size(THUMB_WIDTH_ICON, THUMB_HEIGHT_ICON)
                .toFile(thumbFilePath);
        }
        return file;
    }

    private UploadFile getFile(final String folderId, final String fileNameOrId) {
        UploadFile file = getFileByName(folderId, fileNameOrId);
        if (file == null) {
            file = getFileById(folderId, fileNameOrId);
        }
        return file;
    }

	private UploadFile getFileById(String uploadId) {
		if (ObjectId.isValid(uploadId)) {
			return mongoTemplate.findById(uploadId, UploadFile.class);
		}
		return null;
	}

	private UploadFile getFileById(final String folderId, final String uploadId) {
		if (ObjectId.isValid(uploadId)) {
		    Query query = new Query(Criteria.where("_id").is(QueryId.get(uploadId)).and("folderId").is(folderId));
			return mongoTemplate.findOne(query, UploadFile.class);
		}
		return null;
	}

	private UploadFile getFileByName(final String folderId, final String fileName) {
		Query query = new Query(Criteria.where("fileName").is(fileName).and("folderId").is(folderId));
		return mongoTemplate.findOne(query, UploadFile.class);
	}

	private List<UploadFile> getFilesInFolder(String folderId) {
		Query query = new Query(Criteria.where("folderId").is(folderId));
		query.with(new Sort(Sort.Direction.DESC, "uploadDate"));
		return mongoTemplate.find(query, UploadFile.class);
	}

	private boolean deleteFileById(String folderId, String uploadId) {
	    UploadFile file = getFileById(folderId, uploadId);
		if (file != null) {
			Query query = new Query(Criteria.where("fileName").is(file.getFileName()).and("folderId").is(folderId));
			mongoTemplate.remove(query, UploadFile.class);
			return true;
		}
		return false;
	}

	private UploadFile withAbsolutePath(UploadFile file) {
        final String folderId = file.getFolderId();
        String fileUrl = "";
        if (uploadFolderService.isPublic(folderId)) {
            fileUrl = rosetteSettings.getBaseUrl() + "/api/" + rosetteSettings.getApiVersion() + "/assets/" + folderId + "/" + file.getFileName();
        } else {
            // Need to stream content through cordate server when folder isn't public
            fileUrl = rosetteSettings.getBaseUrl() + "/cordate/api/" + rosetteSettings.getApiVersion() + "/assets/" + folderId + "/" + file.getFileName();
        }
        file.setFileUrl(fileUrl);
        return file;
	}

	private boolean setImageSizeMetadata(UploadFile metaData, byte [] fileData) {
		InputStream inputStream = new ByteArrayInputStream(fileData);
		try {
			BufferedImage image = ImageIO.read(inputStream);
			if (image != null) {
				metaData.setWidth(new Long(image.getWidth()));
				metaData.setHeight(new Long(image.getHeight()));
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return true;
	}

    private boolean setAudioDurationMetadata(UploadFile metaData, byte [] fileData, String mimeType) {
        InputStream inputStream = new ByteArrayInputStream(fileData);
        try {
            long duration = -1;
            if (mimeType.equals("audio/mpeg") || mimeType.equals("audio/mp3")) {
                AudioFileFormat baseFileFormat = new RosetteMpegAudioFileReader().getAudioFileFormat(inputStream, inputStream.available());
                duration = ((long) baseFileFormat.properties().get("duration"))/1000000;
            } else {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                IOUtils.closeQuietly(audioInputStream);
                duration = Math.round(0.5 + frames/format.getFrameRate());
            }
            if (duration <= 0) {
                return false;
            }
            metaData.setDuration(duration);
        } catch (MalformedURLException ignore) {
            return false;
        } catch (UnsupportedAudioFileException ignore) {
            return false;
        } catch (IOException ignore) {
            return false;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return true;
    }
}
