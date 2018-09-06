package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import se.leafcoders.rosette.controller.dto.AssetIn;
import se.leafcoders.rosette.controller.dto.AssetOut;
import se.leafcoders.rosette.persistence.service.AssetService;

@Transactional
@RestController
@RequestMapping(value = "api/assets", produces = "application/json")
public class AssetsController {

    @Autowired
    private AssetService assetService;

    @GetMapping(value = "/{id}")
    public AssetOut getAsset(@PathVariable Long id) {
        return assetService.toOut(assetService.read(id, true));
    }

    @GetMapping()
    public Collection<AssetOut> getAssets(HttpServletRequest request, @RequestParam Long assetFolderId) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return assetService.toOut(assetService.findAllInFolder(assetFolderId, sort, true));
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
}
