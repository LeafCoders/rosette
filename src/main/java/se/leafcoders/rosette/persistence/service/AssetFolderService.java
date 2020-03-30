package se.leafcoders.rosette.persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.AssetFolderIn;
import se.leafcoders.rosette.controller.dto.AssetFolderOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.repository.AssetFolderRepository;
import se.leafcoders.rosette.persistence.repository.AssetRepository;

@Service
public class AssetFolderService extends PersistenceService<AssetFolder, AssetFolderIn, AssetFolderOut> {

    static final Logger logger = LoggerFactory.getLogger(AssetFolderService.class);
    
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AssetRepository assetRepository;

    public AssetFolderService(AssetFolderRepository repository) {
        super(AssetFolder.class, PermissionType::assetFolders, repository);
    }

    @Override
    public AssetFolder create(AssetFolderIn itemIn, boolean checkPermissions) {
        AssetFolder item = super.create(itemIn, checkPermissions);
        fileStorageService.createFolder(item.getId()); // TODO: Any success check here?
        return item;
    }

    @Override
    public ResponseEntity<Void> delete(Long id, boolean checkPermissions) {
        validateFolderIsEmpty(id);
        return super.delete(id, checkPermissions);
    }

    @Override
    protected AssetFolder convertFromInDTO(AssetFolderIn dto, JsonNode rawIn, AssetFolder item) {
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        if (rawIn == null || rawIn.has("allowedMimeTypes")) {
            item.setAllowedMimeTypes(dto.getAllowedMimeTypes());
        }
        if (rawIn == null || rawIn.has("staticFileKey")) {
            item.setStaticFileKey(dto.getStaticFileKey());
        }
        return item;
    }

    @Override
    protected AssetFolderOut convertToOutDTO(AssetFolder item) {
        AssetFolderOut dto = new AssetFolderOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAllowedMimeTypes(item.getAllowedMimeTypes());
        dto.setStaticFileKey(item.getStaticFileKey());
        return dto;
    }

    public boolean isAllowedMimeType(AssetFolder folder, String mimeType) {
        if (folder.getAllowedMimeTypes() == null) {
            return true;
        }
        String[] allowedMimeTypes = folder.getAllowedMimeTypes().split(",");
        if (allowedMimeTypes.length > 0) {
            for (String allowed : allowedMimeTypes) {
                if (mimeType.startsWith(allowed)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private void validateFolderIsEmpty(Long folderId) {
        if (assetRepository.existsByFolderId(folderId)) {
            throw new ForbiddenException(ApiError.UNKNOWN_REASON, "katalogen har filer, kan inte ta bort katalogen.");
        }
    }

}
