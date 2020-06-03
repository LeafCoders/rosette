package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.controller.dto.AssetFileIn;
import se.leafcoders.rosette.controller.dto.AssetOut;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.service.AssetService;
import se.leafcoders.rosette.persistence.service.FileStorageService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/files", produces = "application/json")
public class FilesController {

    private final AssetService assetService;
    private final FileStorageService fileStorageService;

    // Upload file

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public AssetOut createFile(@RequestParam(value = "folderId", required = true) Long folderId,
            @RequestParam(value = "fileName", required = true) String fileName,
            @RequestParam(value = "file", required = true) MultipartFile file, HttpServletResponse response,
            MultipartHttpServletRequest defaultMultipartHttpServletRequest) {
        AssetFileIn item = new AssetFileIn();
        item.setFolderId(folderId);
        item.setFileName(fileName);
        item.setFile(file);
        return assetService.toOut(assetService.createFile(item));
    }

    // Update file

    @PostMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public AssetOut updateFile(@PathVariable Long id,
            @RequestParam(value = "file", required = true) MultipartFile file, HttpServletResponse response,
            MultipartHttpServletRequest defaultMultipartHttpServletRequest) {
        return assetService.toOut(assetService.updateFile(id, file));
    }

    // Download file

    @GetMapping("/{fileKey}/{fileName:.+}")
    public void getFile(@PathVariable String fileKey, @PathVariable String fileName,
            @RequestParam(value = "size", required = false) String thumbSize, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String fileId = assetService.fileIdFromKeyAndFileName(fileKey, fileName);
        Asset asset = assetService.readByFileId(fileId, false);
        // TODO: Check permission is !asset.isPublic
        fileStorageService.streamAssetFile(thumbSize, asset, request, response);
    }

}
