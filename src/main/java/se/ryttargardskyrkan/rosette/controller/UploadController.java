package se.ryttargardskyrkan.rosette.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.UploadRequest;
import se.ryttargardskyrkan.rosette.model.UploadResponse;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import se.ryttargardskyrkan.rosette.service.UploadService;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UploadController extends AbstractController {
	@Autowired
	private UploadService uploadHelper;

	@RequestMapping(value = "uploads/{folder}/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UploadResponse getUpload(@PathVariable String folder, @PathVariable String id, HttpServletRequest request) {
		folder = folder.toLowerCase();
		checkPermission("read:uploads:" + folder + ":" + id);

		GridFSDBFile file = uploadHelper.getFileById(folder, id);
		if (file != null) {
	        return fileToUpload(file, request);
        } else {
			throw new NotFoundException();
		}
	}

	@RequestMapping(value = "uploads/{folder}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UploadResponse> getUploads(@PathVariable String folder, HttpServletRequest request) {
		folder = folder.toLowerCase();
		List<GridFSDBFile> filesInFolder = uploadHelper.getFilesInFolder(folder);
		List<UploadResponse> uploads = new ArrayList<UploadResponse>();
		if (filesInFolder != null) {
			for (GridFSDBFile file : filesInFolder) {
				if (isPermitted("read:uploads:" + folder + ":" + file.getId())) {
					uploads.add(fileToUpload(file, request));
				}
			}
		}
		return uploads;
	}

	@RequestMapping(value = "uploads/{folder}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public UploadResponse postUpload(@PathVariable String folder, @RequestBody UploadRequest upload,
			HttpServletRequest request, HttpServletResponse response) {
		folder = folder.toLowerCase();
		checkPermission("create:uploads:" + folder);
		validate(upload);

		if (uploadHelper.getFileByName(folder, upload.getFileName()) != null) {
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
		GridFSFile file = uploadHelper.storeFile(fileData, upload.getFileName(), upload.getMimeType(), metaData);
		if (file == null) {
			throw new SimpleValidationException(new ValidationError("upload", "upload.invalidFileContent"));
		}
		response.setStatus(HttpStatus.CREATED.value());
		return fileToUpload(file, request);
	}

	@RequestMapping(value = "uploads/{folder}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUpload(@PathVariable String folder, @PathVariable String id, HttpServletResponse response) {
		folder = folder.toLowerCase();
		checkPermission("delete:uploads:" + folder + ":" + id);
		
		if (uploadHelper.deleteFileById(folder, id)) {
			response.setStatus(HttpStatus.OK.value());
		} else {
			throw new NotFoundException();
		}
	}

	private UploadResponse fileToUpload(GridFSFile file, HttpServletRequest request) {
		// Url directly to Rosette server
		String server = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		String path = request.getRequestURI().substring(0, request.getRequestURI().indexOf("/uploads/"));
		String assets = "/assets/" + uploadHelper.getMetadataFolder(file) + "/" + file.getFilename();
		String url = server + path + assets;

		UploadResponse upload = new UploadResponse();
		upload.setId(file.getId().toString());
		upload.setFileName(file.getFilename());
		upload.setFolder(uploadHelper.getMetadataFolder(file));
		upload.setFileUrl(url);
        upload.setMimeType(file.getContentType());
		upload.setFileSize(file.getLength());
		Long width = uploadHelper.getMetadataWidth(file);
		if (width != null) {
			upload.setWidth(width);	
		}
		Long height = uploadHelper.getMetadataHeight(file);
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
