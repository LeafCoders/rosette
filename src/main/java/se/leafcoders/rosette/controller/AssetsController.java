package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import se.leafcoders.rosette.controller.dto.AssetFileIn;
import se.leafcoders.rosette.controller.dto.AssetIn;
import se.leafcoders.rosette.controller.dto.AssetOut;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.service.AssetService;
import se.leafcoders.rosette.persistence.service.FileStorageService;

@RestController
@RequestMapping(value = "api/assets", produces = "application/json")
public class AssetsController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping(value = "/{id}")
    public AssetOut getAsset(@PathVariable Long id) {
        return assetService.toOut(assetService.read(id, true));
    }

    @GetMapping()
    public Collection<AssetOut> getAssets(HttpServletRequest request, @RequestParam Long assetFolderId) {
        return assetService.toOut(assetService.findAllInFolder(assetFolderId, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<AssetOut> postAsset(@RequestBody AssetIn asset) {
        return new ResponseEntity<AssetOut>(assetService.toOut(assetService.create(asset, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public AssetOut putAsset(@PathVariable Long id, HttpServletRequest request) {
        return assetService.toOut(assetService.update(id, AssetIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        return assetService.delete(id, true);
    }

    // Upload file

    @PostMapping(value = "/files", consumes = "multipart/form-data")
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

    @GetMapping(value = "/files/{id}")
    public void getFile(
    		@PathVariable Long id,
    		@RequestParam(value = "size", required = false) String thumbSize,
    		HttpServletRequest request,
    		HttpServletResponse response
	) throws Exception {
    		Asset asset = assetService.read(id, false);
    		// TODO: Check permission is !asset.isPublic
        fileStorageService.streamAssetFile(thumbSize, asset, request, response);
    }

}
