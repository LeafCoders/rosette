package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.service.UploadService;

@RestController
public class AssetController extends ApiV1Controller {
	@Autowired
	private UploadService uploadService;

    @RequestMapping(value = "assets/{folder}/{thumbSize}/{fileName:.+}", method = RequestMethod.GET)
    public void getAssetThumbnail(@PathVariable String folder, @PathVariable String thumbSize, @PathVariable String fileName,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // Only support one thumbnail size for now
        thumbSize = "icon";
        uploadService.streamAssetThumbnail(folder, fileName, thumbSize, request, response);
    }

    @RequestMapping(value = "assets/{folder}/{fileName:.+}", method = RequestMethod.GET)
    public void getAsset(@PathVariable String folder, @PathVariable String fileName,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        uploadService.streamAsset(folder, fileName, request, response);
    }
}
