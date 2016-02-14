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
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import se.leafcoders.rosette.config.RosetteSettings;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.reference.UploadResponseRefs;
import se.leafcoders.rosette.model.upload.UploadRequest;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.MongoDbFileByteRangeSupport;
import se.leafcoders.rosette.util.QueryId;
import se.leafcoders.rosette.util.RosetteMpegAudioFileReader;

@Service
public class UploadService {
	public static final String METADATA_FOLDER_ID = "folderId";
	public static final String METADATA_WIDTH = "width";
	public static final String METADATA_HEIGHT = "height";
    public static final String METADATA_DURATION = "duration";

    @Autowired
    private RosetteSettings rosetteSettings;
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private SecurityService security;
	@Autowired
	private UploadFolderService uploadFolderService;
	@Autowired
	RosetteSettings applicationSettings;

	public UploadResponse create(final String folderId, UploadRequest upload, HttpServletResponse response) {
		response.setStatus(HttpStatus.CREATED.value());

		validateFolderExist(folderId);
		security.checkPermission(new PermissionValue(PermissionType.UPLOADS, PermissionAction.CREATE, folderId));
		checkMimeTypePermission(folderId, upload.getMimeType());

		if (getFileByName(folderId, upload.getFileName()) != null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.alreadyExists"));
		}
        byte[] fileData = upload.getFileData();

		// Set meta data
		DBObject metaData = new BasicDBObject();
		metaData.put(UploadService.METADATA_FOLDER_ID, folderId);
		if (upload.getMimeType().startsWith("image") && !setImageSizeMetadata(metaData, fileData)) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.image.invalidSize"));
		}
        if (upload.getMimeType().startsWith("audio")) {
            setAudioDurationMetadata(metaData, fileData, upload.getMimeType());
        }

		// Store file in gridFs
		GridFSFile file = storeFile(fileData, upload.getFileName(), upload.getMimeType(), metaData);
		if (file == null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.invalidFileContent"));
		}
		response.setStatus(HttpStatus.CREATED.value());
		return fileToUpload(file);
	}

	public UploadResponse read(final String uploadId, boolean checkPermissions) {
		GridFSDBFile file = getFileById(uploadId);
		if (file != null) {
		    if (checkPermissions) {
		        security.checkPermission(new PermissionValue(PermissionType.UPLOADS, PermissionAction.READ, getMetadataFolderId(file), uploadId));
		    }
	        return fileToUpload(file);
        } else {
			throw new NotFoundException("Upload", uploadId);
		}
	}
	
	public List<UploadResponse> readAll(final String folderId) {
		validateFolderExist(folderId);

		List<GridFSDBFile> filesInFolder = getFilesInFolder(folderId);
		List<UploadResponse> uploads = new LinkedList<UploadResponse>();
		if (filesInFolder != null) {
			for (GridFSDBFile file : filesInFolder) {
				if (security.isPermitted(new PermissionValue(PermissionType.UPLOADS, PermissionAction.READ, folderId, file.getId().toString()))) {
					uploads.add(fileToUpload(file));
				}
			}
		}
		return uploads;
	}

	public void delete(final String folderId, final String uploadId, HttpServletResponse response) {
		validateFolderExist(folderId);
		security.checkPermission(new PermissionValue(PermissionType.UPLOADS, PermissionAction.DELETE, folderId, uploadId));
		security.checkNotReferenced(uploadId, UploadResponse.class);

		if (deleteFileById(folderId, uploadId)) {
			response.setStatus(HttpStatus.OK.value());
		} else {
			throw new NotFoundException("Upload", uploadId);
		}
	}

	public boolean containsUploads(String folderId, UploadResponseRefs uploads) {
		List<String> uploadIdsInFolder = getFileIdsInFolder(folderId);
		for (UploadResponse upload : uploads) {
			if (!uploadIdsInFolder.contains(upload.getId())) {
				return false;
			}
		}
		return true;
	}

	public List<String> getFileIdsInFolder(String folderId) {
		List<GridFSDBFile> filesInFolder = getFilesInFolder(folderId);
		List<String> uploadIds = new LinkedList<String>();
		if (filesInFolder != null) {
			for (GridFSDBFile file : filesInFolder) {
				uploadIds.add(file.getId().toString());
			}
		}
		return uploadIds;
	}

	public void streamAsset(final String folderId, final String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		validateFolderExist(folderId);
		if (uploadFolderService.isPublic(folderId) == false) {
			security.checkPermission(
					new PermissionValue(PermissionType.ASSETS, PermissionAction.READ, folderId),
					new PermissionValue(PermissionType.UPLOADS, PermissionAction.READ, folderId));
		}

		GridFSDBFile file = getFileByName(folderId, fileName);
		if (file == null) {
			// Find file by id if not found by file name
			file = getFileById(folderId, fileName);
		}
		if (file != null) {
			try {
		        response.addHeader("Cache-Control", "public");
		        if (rosetteSettings.getUploadCacheMaxAge() > 0) {
		        	response.addHeader("Cache-Control", "max-age=" + rosetteSettings.getUploadCacheMaxAge()); 
		        }
		        
		        // Indicate the browser to view the file
		        response.addHeader("Content-Disposition", "inline; " + file.getFilename());
		        
				new MongoDbFileByteRangeSupport().with(request).with(response).serveResource(file, file.getContentType());
			} catch (ClientAbortException abortException) {
				return;
			}
        } else {
			throw new NotFoundException("Upload", fileName);
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

	private GridFSFile storeFile(byte [] fileData, String fileName, String mimeType, DBObject metaData) {
		InputStream inputStream = new ByteArrayInputStream(fileData);
		try {
			return gridFsTemplate.store(inputStream, fileName, mimeType, metaData);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private GridFSDBFile getFileById(String uploadId) {
		if (ObjectId.isValid(uploadId)) {
			return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(QueryId.get(uploadId))));
		}
		return null;
	}

	private GridFSDBFile getFileById(String folderId, String uploadId) {
		if (ObjectId.isValid(uploadId)) {
			return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(QueryId.get(uploadId)).and("metadata." + METADATA_FOLDER_ID).is(folderId)));
		}
		return null;
	}

	private GridFSDBFile getFileByName(String folderId, String fileName) {
		Query query = new Query(Criteria.where("filename").is(fileName).and("metadata." + METADATA_FOLDER_ID).is(folderId));
		return gridFsTemplate.findOne(query);
	}

	private List<GridFSDBFile> getFilesInFolder(String folderId) {
		Query query = new Query(Criteria.where("metadata." + METADATA_FOLDER_ID).is(folderId));
		query.with(new Sort(Sort.Direction.DESC, "uploadDate"));
		return gridFsTemplate.find(query);
	}

	private boolean deleteFileById(String folderId, String uploadId) {
		if (getFileById(folderId, uploadId) != null) {
			Query query = new Query(Criteria.where("_id").is(QueryId.get(uploadId)).and("metadata." + METADATA_FOLDER_ID).is(folderId));
			gridFsTemplate.delete(query);
			return true;
		}
		return false;
	}

	private String getMetadataFolderId(GridFSFile file) {
		return file.getMetaData().get(METADATA_FOLDER_ID).toString();
	}

	private Long getMetadataWidth(GridFSFile file) {
		Object width = file.getMetaData().get(METADATA_WIDTH);
		if (width != null) {
			return Long.parseLong(width.toString());	
		}
		return null;
	}

	private Long getMetadataHeight(GridFSFile file) {
		Object height = file.getMetaData().get(METADATA_HEIGHT);
		if (height != null) {
			return Long.parseLong(height.toString());	
		}
		return null;
	}

    private Long getMetadataDuration(GridFSFile file) {
        Object duration = file.getMetaData().get(METADATA_DURATION);
        if (duration != null) {
            return Long.parseLong(duration.toString());   
        }
        return null;
    }

	private UploadResponse fileToUpload(GridFSFile file) {
		final String folderId = getMetadataFolderId(file);
		UploadResponse upload = new UploadResponse();
		upload.setId(file.getId().toString());
		upload.setFileName(file.getFilename());
		upload.setFolderId(folderId);
		
		String fileUrl = "";
		if (uploadFolderService.isPublic(folderId)) {
		    fileUrl = rosetteSettings.getBaseUrl() + "/api/" + rosetteSettings.getApiVersion() + "/assets/" + folderId + "/" + file.getFilename();
		} else {
			// Need to stream content through cordate server when folder isn't public
	        fileUrl = rosetteSettings.getBaseUrl() + "/cordate/api/" + rosetteSettings.getApiVersion() + "/assets/" + folderId + "/" + file.getFilename();
		}
		upload.setFileUrl(fileUrl);
		
        upload.setMimeType(file.getContentType());
		upload.setFileSize(file.getLength());
		Long width = getMetadataWidth(file);
		if (width != null) {
			upload.setWidth(width);	
		}
		Long height = getMetadataHeight(file);
		if (height != null) {
			upload.setHeight(height);	
		}
        Long duration = getMetadataDuration(file);
        if (duration != null) {
            upload.setDuration(duration);   
        }
        return upload;
	}

	private boolean setImageSizeMetadata(DBObject metaData, byte [] fileData) {
		InputStream inputStream = new ByteArrayInputStream(fileData);
		try {
			BufferedImage image = ImageIO.read(inputStream);
			if (image != null) {
				metaData.put(UploadService.METADATA_WIDTH, String.valueOf(image.getWidth()));
				metaData.put(UploadService.METADATA_HEIGHT, String.valueOf(image.getHeight()));
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

    private boolean setAudioDurationMetadata(DBObject metaData, byte [] fileData, String mimeType) {
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
            metaData.put(UploadService.METADATA_DURATION, String.valueOf(duration));
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
