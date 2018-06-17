package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import se.leafcoders.rosette.controller.dto.AssetFileIn;
import se.leafcoders.rosette.controller.dto.AssetOut;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.service.AssetService;
import se.leafcoders.rosette.persistence.service.FileStorageService;

@RestController
@RequestMapping(value = "api/files", produces = "application/json")
public class FilesController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private FileStorageService fileStorageService;

    // Upload file

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AssetOut> postFile(
        @RequestParam(value = "folderId", required = true) Long folderId,
        @RequestParam(value = "fileName", required = true) String fileName,
        @RequestParam(value = "file", required = true) MultipartFile file,
        HttpServletResponse response,
        MultipartHttpServletRequest defaultMultipartHttpServletRequest
    ) {
        AssetFileIn item = new AssetFileIn();
        item.setFolderId(folderId);
        item.setFileName(fileName);
        item.setFile(file);

        return new ResponseEntity<AssetOut>(assetService.toOut(assetService.createFile(item)), HttpStatus.CREATED);
    }

    // Download file

    @GetMapping("/{fileId:.+}")
    public void getFile(
    		@PathVariable String fileId,
    		@RequestParam(value = "size", required = false) String thumbSize,
    		HttpServletRequest request,
    		HttpServletResponse response
	) throws Exception {
        Asset asset = assetService.readByFileId(fileId, false);
    		// TODO: Check permission is !asset.isPublic
        fileStorageService.streamAssetFile(thumbSize, asset, request, response);
    }

}
