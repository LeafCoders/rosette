package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.model.UploadFolder;
import se.leafcoders.rosette.service.UploadFolderService;
import java.util.List;

@Controller
public class UploadFolderController extends AbstractController {

	@Autowired
	private UploadFolderService uploadFolderService;

	@RequestMapping(value = "uploadFolders", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UploadFolder> getUploadFolders() {
		return uploadFolderService.getAllPermitted();
	}
}
