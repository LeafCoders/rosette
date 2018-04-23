package se.leafcoders.rosette.persistence.service;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.RosetteSettings;
import se.leafcoders.rosette.controller.dto.AssetFileIn;
import se.leafcoders.rosette.controller.dto.AssetIn;
import se.leafcoders.rosette.controller.dto.AssetOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SingleValidationException;
import se.leafcoders.rosette.exception.ValidationError;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionId;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Asset.AssetType;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.repository.AssetRepository;
import se.leafcoders.rosette.util.FileMetadataReader;

@Service
public class AssetService extends PersistenceService<Asset, AssetIn, AssetOut> {

    static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    @Autowired
    private RosetteSettings rosetteSettings;

    @Autowired
    private AssetFolderService assetFolderService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    public AssetService(AssetRepository repository) {
        super(Asset.class, PermissionType.ASSETS, repository);
    }

    private AssetRepository repo() {
        return (AssetRepository) repository;
    }

    public Asset readByFileId(String fileId, boolean checkPermissions) {
        if (fileId == null) {
            return null;
        }
        Asset item = repo().findOneByFileId(fileId);
        if (item == null) {
            throw new NotFoundException("Asset with fileId (" + fileId + ") was not found.");
        }
        if (checkPermissions) {
            checkPermissions(itemPermissions(PermissionAction.READ, new PermissionId<Asset>(item)));
        }
        return item;
    }
    
    public List<Asset> findAllInFolder(Long assetFolderId, Sort sort, boolean checkPermissions) {
        return readManyCheckPermissions(repo().findByFolderId(assetFolderId, sort), checkPermissions);
    }

    public String urlOfAsset(Asset asset) {
        if (asset.getType() == AssetType.FILE) {
            return rosetteSettings.getBaseUrl() + "/api/assets/files/" + asset.getFileId();
        } else {
            return asset.getUrl();
        }
    }

    protected void extraValidation(Asset item) {
    }
    
    @Override
    protected Asset convertFromInDTO(AssetIn dto, JsonNode rawIn, Asset item) {
        if (rawIn == null || rawIn.has("type")) {
            item.setType(Asset.AssetType.valueOf(dto.getType()));
        }
        if (rawIn == null || rawIn.has("url")) {
            item.setUrl(dto.getUrl());
        }
        
        item.setMimeType("image/jpg");
        
        return item;
    }

    @Override
    protected AssetOut convertToOutDTO(Asset item) {
        AssetOut dto = new AssetOut();
        dto.setId(item.getId());
        dto.setType(item.getType().name());

        dto.setMimeType(item.getMimeType());
        dto.setFileName(item.getFileName());
        dto.setUrl(urlOfAsset(item));

        dto.setFileSize(item.getFileSize());
        dto.setWidth(item.getWidth());
        dto.setHeight(item.getHeight());
        dto.setDuration(item.getDuration());
        return dto;
    }

    public Asset createFile(AssetFileIn fileIn) {
        final MultipartFile file = fileIn.getFile();
        final Long folderId = fileIn.getFolderId();
        final String fileName = fileIn.getFileName();
        final String mimeType = file.getContentType();

        checkAnyPermission(
            new PermissionValue(PermissionType.ASSETS, PermissionAction.CREATE),
            new PermissionValue(PermissionType.ASSET_FOLDERS_FILES, PermissionAction.CREATE).forId(folderId)
        );

        AssetFolder folder = assetFolderService.read(folderId, false);
        validateFolderExist(folder);
        checkMimeTypeAllowed(folder, mimeType);

        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_NOT_READABLE));
        }

        Asset asset = new Asset();
        asset.setType(Asset.AssetType.FILE);
        asset.setFolderId(folderId);
        asset.setFileId(generateFileId(fileName));
        asset.setFileName(fileName);
        asset.setMimeType(mimeType);
        asset.setFileSize(new Long(fileData.length));
        readMetadata(fileData, asset);
        securityService.validate(asset, null);

        boolean stored = fileStorageService.storeFile(folderId, asset.getFileId(), fileData);
        if (!stored) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_INVALID_CONTENT));
        }

        try {
            return repo().save(asset);
        } catch (org.springframework.dao.DuplicateKeyException ignore) {
            throw new ForbiddenException(ApiError.CREATE_ALREADY_EXIST, fileName);
        }
    }

    private String generateFileId(String fileName) {
        String fileId;
        int maxTries = 10;
        do {
            final byte[] rand = String.format("%05d", (int) Math.floor(Math.random() * 100000d)).getBytes();
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < rand.length; i++) {
                sb.append(Integer.toString((rand[i] & 0x0f), 16).substring(1));
            }
            fileId = rand.toString().substring(3, 8) + "-" + fileName;
            maxTries--;
        } while (repo().existsByFileId(fileId) && maxTries >= 0);
        return fileId;
    }
    
    private void validateFolderExist(AssetFolder folder) {
        if (!fileStorageService.folderExist(folder.getId())) {
            throw new NotFoundException(AssetFolder.class, folder.getId());
        }
    }

    private void checkMimeTypeAllowed(AssetFolder folder, String mimeType) {
        if (!assetFolderService.isAllowedMimeType(folder, mimeType)) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_MIMETYPE_NOT_ALLOWED));
        }
    }

    private void readMetadata(byte[] fileData, Asset asset) {
        if (asset.getMimeType().startsWith("image")) {
            Long[] dimensions = FileMetadataReader.readImageSize(fileData);
            if (dimensions != null) {
                asset.setWidth(dimensions[0]);
                asset.setHeight(dimensions[1]);
            } else {
                throw new SingleValidationException(new ValidationError("file", ApiString.FILE_INVALID_DIMENSION));
            }
        }
        if (asset.getMimeType().startsWith("audio")) {
            Long duration = FileMetadataReader.readAudioDuration(fileData, asset.getMimeType());
            if (duration != null) {
                asset.setDuration(duration);
            } else {
                logger.warn("Could not read duration of audio file '" + asset.getFileName() + "'.");
            }
        }
    }
}
