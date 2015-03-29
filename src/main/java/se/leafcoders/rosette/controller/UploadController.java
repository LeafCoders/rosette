package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.model.upload.UploadRequest;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.service.UploadService;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UploadController extends AbstractController {
	@Autowired
	private UploadService uploadService;

	@RequestMapping(value = "uploads/{folder}/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UploadResponse getUpload(@PathVariable String folder, @PathVariable String id) {
		return uploadService.read(id);
	}

	@RequestMapping(value = "uploads/{folder}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UploadResponse> getUploads(@PathVariable String folder) {
		return uploadService.readAll(folder);
	}

	@RequestMapping(value = "uploads/{folder}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public UploadResponse postUpload(@PathVariable String folder, @RequestBody UploadRequest upload, HttpServletResponse response) {
		return uploadService.create(folder, upload, response);
	}

	@RequestMapping(value = "uploads/{folder}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUpload(@PathVariable String folder, @PathVariable String id, HttpServletResponse response) {
		uploadService.delete(folder, id, response);
	}
}
