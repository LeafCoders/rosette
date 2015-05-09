package se.leafcoders.rosette.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.application.ApplicationSettings;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.reference.UploadResponseRefs;
import se.leafcoders.rosette.model.upload.UploadRequest;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import util.QueryId;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Service
public class UploadService {
	public static final String METADATA_FOLDER_ID = "folderId";
	public static final String METADATA_WIDTH = "width";
	public static final String METADATA_HEIGHT = "height";

	@Value("${rosette.baseUrl}")
	private String baseUrl;
	
	@Value("${rosette.apiVersion}")
	private String apiVersion;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private SecurityService security;
	@Autowired
	private UploadFolderService uploadFolderService;
	@Autowired
	ApplicationSettings applicationSettings;

	public UploadResponse create(final String folderId, UploadRequest upload, HttpServletResponse response) {
		response.setStatus(HttpStatus.CREATED.value());

		validateFolderExist(folderId);
		security.checkPermission(PermissionType.UPLOADS, PermissionAction.CREATE, folderId);
		checkMimeTypePermission(folderId, upload.getMimeType());

		if (getFileByName(folderId, upload.getFileName()) != null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.alreadyExists"));
		}
		byte[] fileData = upload.getFileDataAsBytes(); 

		// Set meta data
		DBObject metaData = new BasicDBObject();
		metaData.put(UploadService.METADATA_FOLDER_ID, folderId);
		if (upload.getMimeType().startsWith("image") && !setImageSizeMetadata(metaData, fileData)) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.image.invalidSize"));
		}

		// Store file in gridFs
		GridFSFile file = storeFile(fileData, upload.getFileName(), upload.getMimeType(), metaData);
		if (file == null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.invalidFileContent"));
		}
		response.setStatus(HttpStatus.CREATED.value());
		return fileToUpload(file);
	}

	public UploadResponse read(final String uploadId) {
		GridFSDBFile file = getFileById(uploadId);
		if (file != null) {
			security.checkPermission(PermissionType.UPLOADS, PermissionAction.READ, getMetadataFolderId(file), uploadId);
	        return fileToUpload(file);
        } else {
			throw new NotFoundException();
		}
	}
	
	public List<UploadResponse> readAll(final String folderId) {
		validateFolderExist(folderId);

		List<GridFSDBFile> filesInFolder = getFilesInFolder(folderId);
		List<UploadResponse> uploads = new LinkedList<UploadResponse>();
		if (filesInFolder != null) {
			for (GridFSDBFile file : filesInFolder) {
				if (security.isPermitted(PermissionType.UPLOADS, PermissionAction.READ, folderId, file.getId().toString())) {
					uploads.add(fileToUpload(file));
				}
			}
		}
		return uploads;
	}

	public void delete(final String folderId, final String uploadId, HttpServletResponse response) {
		validateFolderExist(folderId);
		security.checkPermission(PermissionType.UPLOADS, PermissionAction.DELETE, folderId, uploadId);
		security.checkNotReferenced(uploadId, PermissionType.UPLOADS);

		if (deleteFileById(folderId, uploadId)) {
			response.setStatus(HttpStatus.OK.value());
		} else {
			throw new NotFoundException();
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

	public void streamAsset(final String folderId, final String fileName, HttpServletResponse response) {
		validateFolderExist(folderId);
		if (uploadFolderService.isPublic(folderId) == false) {
			if (!(security.isPermitted(PermissionType.ASSETS, PermissionAction.READ, folderId) ||
					security.isPermitted(PermissionType.UPLOADS, PermissionAction.READ, folderId))) {
				security.throwPermissionMissing(
						security.getPermissionString(PermissionType.ASSETS, PermissionAction.READ, folderId),
						security.getPermissionString(PermissionType.UPLOADS, PermissionAction.READ, folderId));
			}
		}

		GridFSDBFile file = getFileByName(folderId, fileName);
		if (file == null) {
			// Find file by id if not found by file name
			file = getFileById(folderId, fileName);
		}
		if (file != null) {
			try {
		        response.addHeader("Cache-Control", "public");
		        if (applicationSettings.useUploadCacheMaxAge()) {
		        	response.addHeader("Cache-Control", "max-age=604800"); // One week 
		        }
		        response.addHeader("Content-disposition", "attachment; filename=\"" + file.getFilename() + "\"");
		        response.setContentType(file.getContentType());
		        response.setContentLength((int)file.getLength());

	            response.getOutputStream().write(IOUtils.toByteArray(file.getInputStream()));
	            response.getOutputStream().flush();
			} catch (IOException e) {
				throw new NotFoundException();
			}
        } else {
			throw new NotFoundException();
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

	private UploadResponse fileToUpload(GridFSFile file) {
		final String folderId = getMetadataFolderId(file);
		UploadResponse upload = new UploadResponse();
		upload.setId(file.getId().toString());
		upload.setFileName(file.getFilename());
		upload.setFolderId(folderId);
		if (uploadFolderService.isPublic(folderId)) {
			upload.setFileUrl(baseUrl + "/api/" + apiVersion + "/assets/" + folderId + "/" + file.getFilename());
		} else {
			// Need to stream content through cordate server when folder isn't public
			upload.setFileUrl(baseUrl + "/cordate/api/" + apiVersion + "/assets/" + folderId + "/" + file.getFilename());
		}
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
}
