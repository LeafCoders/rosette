package se.leafcoders.rosette.endpoint.assetfolder;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/assetFolders", produces = "application/json")
public class AssetFolderController {

    private final AssetFolderService assetFolderService;

    @GetMapping(value = "/{id}")
    public AssetFolderOut getAssetFolder(@PathVariable Long id) {
        return assetFolderService.toOut(assetFolderService.read(id, true));
    }

    @GetMapping()
    public Collection<AssetFolderOut> getAssetFolders(HttpServletRequest request) {
        return assetFolderService.toOut(assetFolderService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public AssetFolderOut postAssetFolder(@RequestBody AssetFolderIn assetFolder) {
        return assetFolderService.toOut(assetFolderService.create(assetFolder, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public AssetFolderOut putAssetFolder(@PathVariable Long id, HttpServletRequest request) {
        return assetFolderService.toOut(assetFolderService.update(id, AssetFolderIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssetFolder(@PathVariable Long id) {
        assetFolderService.delete(id, true);
    }
}
