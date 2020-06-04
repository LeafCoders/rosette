package se.leafcoders.rosette.endpoint.asset;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/assets", produces = "application/json")
public class AssetController {

    private final AssetService assetService;

    @GetMapping(value = "/{id}")
    public AssetOut getAsset(@PathVariable Long id) {
        return assetService.toOut(assetService.read(id, true));
    }

    @GetMapping()
    public Collection<AssetOut> getAssets(HttpServletRequest request, @RequestParam Long assetFolderId) {
        Sort sort = Sort.by("id").descending();
        return assetService.toOut(assetService.findAllInFolder(assetFolderId, sort, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public AssetOut postAsset(@RequestBody AssetIn asset) {
        return assetService.toOut(assetService.create(asset, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public AssetOut putAsset(@PathVariable Long id, HttpServletRequest request) {
        return assetService.toOut(assetService.update(id, AssetIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAsset(@PathVariable Long id) {
        assetService.delete(id, true);
    }
}
