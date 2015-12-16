package se.leafcoders.rosette.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.upload.UploadRequest;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.service.UploadService;

@Controller
public class UploadController extends AbstractController {
	@Autowired
	private UploadService uploadService;

	@RequestMapping(value = "uploads/{folder}/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UploadResponse getUpload(@PathVariable String folder, @PathVariable String id) {
		return uploadService.read(id, true);
	}

	@RequestMapping(value = "uploads/{folder}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UploadResponse> getUploads(@PathVariable String folder) {
		return uploadService.readAll(folder);
	}

	@RequestMapping(value = "uploads/{folder}", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public UploadResponse postUpload(@PathVariable String folder,
	        @RequestParam(value="file", required=true) MultipartFile file,
            @RequestParam(value="fileName", required=true) String fileName,
	        HttpServletResponse response,
	        MultipartHttpServletRequest defaultMultipartHttpServletRequest
        ) {

	    UploadRequest upload = new UploadRequest();
	    try {
            upload.setFileData(file.getBytes());
            upload.setFileName(fileName);
            upload.setMimeType(file.getContentType());
        } catch (IOException e) {
            throw new SimpleValidationException(new ValidationError("upload", "upload.file.failedToRead"));            
        }
	    
		return uploadService.create(folder, upload, response);
	}

	@RequestMapping(value = "uploads/{folder}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUpload(@PathVariable String folder, @PathVariable String id, HttpServletResponse response) {
		uploadService.delete(folder, id, response);
	}
}
