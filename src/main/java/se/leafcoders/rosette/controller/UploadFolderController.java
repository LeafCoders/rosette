package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.upload.UploadFolder;
import se.leafcoders.rosette.service.UploadFolderService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class UploadFolderController extends AbstractController {

	@Autowired
	private UploadFolderService uploadFolderService;

	@RequestMapping(value = "uploadFolders/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UploadFolder getUploadFolder(@PathVariable String id) {
		return uploadFolderService.read(id);
	}

	@RequestMapping(value = "uploadFolders", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UploadFolder> getUploadFolders(HttpServletRequest request) {
		return uploadFolderService.readMany(new ManyQuery(request));
	}
	
	@RequestMapping(value = "uploadFolders", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public UploadFolder postUploadFolder(@RequestBody UploadFolder uploadFolder, HttpServletResponse response) {
		return uploadFolderService.create(uploadFolder, response);
	}

	@RequestMapping(value = "uploadFolders/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putBooking(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		uploadFolderService.update(id, request, response);
	}

	@RequestMapping(value = "uploadFolders/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBooking(@PathVariable String id, HttpServletResponse response) {
		uploadFolderService.delete(id, response);
	}

}
