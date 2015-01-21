package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.service.UploadService;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AssetController extends AbstractController {
	@Autowired
	private UploadService uploadService;

	@RequestMapping(value = "assets/{folder}/{fileName:.+}", method = RequestMethod.GET)
	public void getAsset(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) {
		uploadService.streamAsset(folder, fileName, response);
	}
}
