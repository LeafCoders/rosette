package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RestController;

import se.leafcoders.rosette.controller.dto.AssetFolderIn;
import se.leafcoders.rosette.controller.dto.AssetFolderOut;
import se.leafcoders.rosette.persistence.service.AssetFolderService;

@RestController
@RequestMapping(value = "api/assetFolders", produces = "application/json")
public class AssetFoldersController {

    @Autowired
    private AssetFolderService assetFolderService;

    @GetMapping(value = "/{id}")
    public AssetFolderOut getAssetFolder(@PathVariable Long id) {
        return assetFolderService.toOut(assetFolderService.read(id, true));
    }

    @GetMapping()
    public Collection<AssetFolderOut> getAssetFolders(HttpServletRequest request) {
        return assetFolderService.toOut(assetFolderService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<AssetFolderOut> postAssetFolder(@RequestBody AssetFolderIn assetFolder) {
        return new ResponseEntity<AssetFolderOut>(assetFolderService.toOut(assetFolderService.create(assetFolder, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public AssetFolderOut putAssetFolder(@PathVariable Long id, HttpServletRequest request) {
        return assetFolderService.toOut(assetFolderService.update(id, AssetFolderIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAssetFolder(@PathVariable Long id) {
        return assetFolderService.delete(id, true);
    }
}
