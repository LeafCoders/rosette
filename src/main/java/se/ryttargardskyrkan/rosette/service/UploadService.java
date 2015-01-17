package se.ryttargardskyrkan.rosette.service;

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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.exception.ForbiddenException;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.UploadRequest;
import se.ryttargardskyrkan.rosette.model.UploadResponse;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Service
public class UploadService {
	public static final String METADATA_FOLDER = "folder";
	public static final String METADATA_WIDTH = "width";
	public static final String METADATA_HEIGHT = "height";

	@Value("${rosette.baseUrl}")
	private String baseUrl;
	
	@Value("${rosette.apiVersion}")
	private String apiVersion;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private SecurityService security;
	@Autowired
	private UploadFolderService uploadFolderService;

	public UploadResponse create(final String folder, UploadRequest upload, HttpServletResponse response) {
		response.setStatus(HttpStatus.CREATED.value());

		sanitizeAndValidateFolder(folder);
		security.checkPermission("create:uploads:" + folder);
		checkMimeTypePermission(folder, upload.getMimeType());

		if (getFileByName(folder, upload.getFileName()) != null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.alreadyExists"));
		}
		byte[] fileData = upload.getFileDataAsBytes(); 

		// Set meta data
		DBObject metaData = new BasicDBObject();
		metaData.put(UploadService.METADATA_FOLDER, folder);
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

	public UploadResponse read(final String id) {
		GridFSDBFile file = getFileById(id);
		if (file != null) {
			security.checkPermission("read:uploads:" + getMetadataFolder(file) + ":" + id);
	        return fileToUpload(file);
        } else {
			throw new NotFoundException();
		}
	}
	
	public List<UploadResponse> readAll(final String folder) {
		sanitizeAndValidateFolder(folder);

		List<GridFSDBFile> filesInFolder = getFilesInFolder(folder);
		List<UploadResponse> uploads = new LinkedList<UploadResponse>();
		if (filesInFolder != null) {
			for (GridFSDBFile file : filesInFolder) {
				if (security.isPermitted("read:uploads:" + folder + ":" + file.getId())) {
					uploads.add(fileToUpload(file));
				}
			}
		}
		return uploads;
	}

	public void delete(final String folder, final String id, HttpServletResponse response) {
		sanitizeAndValidateFolder(folder);
		security.checkPermission("delete:uploads:" + folder + ":" + id);
		security.checkNotReferenced(id, "uploads");

		if (deleteFileById(folder, id)) {
			response.setStatus(HttpStatus.OK.value());
		} else {
			throw new NotFoundException();
		}
	}

	public boolean containsUploads(String folder, List<ObjectReference<UploadResponse>> uploadIdRefs) {
		List<String> uploadIdsInFolder = getFileIdsInFolder(folder);
		for (ObjectReference<UploadResponse> uploadIdRef : uploadIdRefs) {
			if (!uploadIdsInFolder.contains(uploadIdRef.getIdRef())) {
				return false;
			}
		}
		return true;
	}

	public List<String> getFileIdsInFolder(String folder) {
		List<GridFSDBFile> filesInFolder = getFilesInFolder(folder);
		List<String> uploadIds = new LinkedList<String>();
		if (filesInFolder != null) {
			for (GridFSDBFile file : filesInFolder) {
				uploadIds.add(file.getId().toString());
			}
		}
		return uploadIds;
	}

	public void streamAsset(final String folder, final String fileName, HttpServletResponse response) {
		sanitizeAndValidateFolder(folder);
		if (uploadFolderService.isPublic(folder) == false) {
			if (!(security.isPermitted("read:assets:" + folder) || security.isPermitted("read:uploads:" + folder))) {
				throw new ForbiddenException("Missing permission: One of read:assets:" + folder + " or read:uploads:" + folder + " must be permitted.");
			}
		}

		GridFSDBFile file = getFileByName(folder, fileName);
		if (file == null) {
			// Find file by id if not found by file name
			file = getFileById(folder, fileName);
		}
		if (file != null) {
			try {
		        response.addHeader("Cache-Control", "public");
//TODO: Remove when production		        response.addHeader("Cache-Control", "max-age=86400"); // One day
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

	private void sanitizeAndValidateFolder(String folderName) {
		folderName = folderName.toLowerCase();
		if (!uploadFolderService.folderExist(folderName)) {
			throw new SimpleValidationException(new ValidationError("upload", "uploadFolder.dontExist"));
		}
	}

	private void checkMimeTypePermission(final String folderName, final String mimeType) {
		if (!uploadFolderService.isPermittedMimeType(folderName, mimeType)) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.mimeTypeNotAllowed"));
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

	private GridFSDBFile getFileById(String id) {
		if (ObjectId.isValid(id)) {
			return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));
		}
		return null;
	}

	private GridFSDBFile getFileById(String folder, String id) {
		if (ObjectId.isValid(id)) {
			return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id)).and("metadata." + METADATA_FOLDER).is(folder)));
		}
		return null;
	}

	private GridFSDBFile getFileByName(String folder, String fileName) {
		Query query = new Query(Criteria.where("filename").is(fileName).and("metadata." + METADATA_FOLDER).is(folder));
		return gridFsTemplate.findOne(query);
	}

	private List<GridFSDBFile> getFilesInFolder(String folder) {
		Query query = new Query(Criteria.where("metadata." + METADATA_FOLDER).is(folder));
		return gridFsTemplate.find(query);
	}

	private boolean deleteFileById(String folder, String id) {
		if (getFileById(folder, id) != null) {
			Query query = new Query(Criteria.where("_id").is(new ObjectId(id)).and("metadata." + METADATA_FOLDER).is(folder));
			gridFsTemplate.delete(query);
			return true;
		}
		return false;
	}

	private String getMetadataFolder(GridFSFile file) {
		return file.getMetaData().get(METADATA_FOLDER).toString();
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
		final String folderName = getMetadataFolder(file);
		UploadResponse upload = new UploadResponse();
		upload.setId(file.getId().toString());
		upload.setFileName(file.getFilename());
		upload.setFolderName(folderName);
		if (uploadFolderService.isPublic(folderName)) {
			upload.setFileUrl(baseUrl + "/api/" + apiVersion + "/assets/" + folderName + "/" + file.getFilename());
		} else {
			// Need to stream content through cordate server when folder isn't public
			upload.setFileUrl(baseUrl + "/cordate/api/" + apiVersion + "/assets/" + folderName + "/" + file.getFilename());
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
