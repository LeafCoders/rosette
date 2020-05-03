package se.leafcoders.rosette.persistence.service;

import java.io.IOException;
import java.text.MessageFormat;
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
        super(Asset.class, PermissionType::assets, repository);
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
            checkPermissions(itemReadUpdateDeletePermissions(PermissionAction.READ, new PermissionId<Asset>(item)));
        }
        return item;
    }

    public List<Asset> findAllInFolder(Long assetFolderId, Sort sort, boolean checkPermissions) {
        return readManyCheckPermissions(repo().findByFolderId(assetFolderId, sort), checkPermissions);
    }

    public String urlOfAsset(Asset asset) {
        if (asset == null) {
            return null;
        } else if (asset.getType() == AssetType.FILE) {
            return rosetteSettings.getBaseUrl() + "api/files/" + asset.getFileId().replaceFirst("-", "/");
        } else {
            return asset.getUrl();
        }
    }

    @Override
    protected Asset convertFromInDTO(AssetIn dto, JsonNode rawIn, Asset item) {
        // Only set when create
        if (item.getId() == null) {
            if (rawIn == null || rawIn.has("type")) {
                item.setType(AssetType.valueOf(dto.getType()));
                if (item.getType() == AssetType.URL) {
                    item.setMimeType("application/url");
                }
            }
            if (rawIn == null || rawIn.has("folderId")) {
                item.setFolderId(dto.getFolderId());
            }
        }
        // Update is only supported for type URL
        if (item.getType() == AssetType.URL) {
            if (rawIn == null || rawIn.has("url")) {
                item.setUrl(dto.getUrl());
            }
        }
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
        
        dto.setIsImageFile(item.isImageFile());
        dto.setIsAudioFile(item.isAudioFile());
        dto.setIsTextFile(item.isTextFile());

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
                PermissionType.assets().create(),
                PermissionType.assetFolders().manageAssets().forId(folderId)
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
        asset.setFileId(generateFileId(folder, fileName));
        asset.setFileVersion(1);
        asset.setFileName(fileName);
        asset.setMimeType(mimeType);
        asset.setFileSize(Long.valueOf(fileData.length));
        readMetadata(fileData, asset);
        securityService.validate(asset, null);

        boolean stored = fileStorageService.storeFile(folderId, asset.fileNameOnDisk(), fileData);
        if (!stored) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_INVALID_CONTENT));
        }

        try {
            return repo().save(asset);
        } catch (org.springframework.dao.DuplicateKeyException ignore) {
            throw new ForbiddenException(ApiError.CREATE_ALREADY_EXIST, fileName);
        }
    }

    public Asset updateFile(Long assetId, MultipartFile file) {
        final Asset existingAsset = read(assetId, true);
        final Long folderId = existingAsset.getFolderId();
        final String mimeType = file.getContentType();

        checkAnyPermission(
                PermissionType.assets().update(),
                PermissionType.assetFolders().manageAssets().forId(folderId)
        );

        if (!existingAsset.isTextFile()) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_ONLY_TEXTFILES_ARE_UPDATEABLE));
        }
        if (!existingAsset.getMimeType().equals(mimeType)) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_MIMETYPE_NOT_ALLOWED));
        }

        AssetFolder folder = assetFolderService.read(folderId, false);
        validateFolderExist(folder);
        checkMimeTypeAllowed(folder, mimeType);

        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_NOT_READABLE));
        }

        existingAsset.setFileVersion(existingAsset.getFileVersion() + 1);
        existingAsset.setMimeType(mimeType);
        existingAsset.setFileSize(Long.valueOf(fileData.length));
        readMetadata(fileData, existingAsset);
        securityService.validate(existingAsset, null);

        boolean stored = fileStorageService.storeFile(folderId, existingAsset.fileNameOnDisk(), fileData);
        if (!stored) {
            throw new SingleValidationException(new ValidationError("file", ApiString.FILE_INVALID_CONTENT));
        }

        try {
            return repo().save(existingAsset);
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            throw notFoundException(assetId);
        }
    }

    private String generateFileId(AssetFolder assetFolder, String fileName) {
        if (assetFolder.getStaticFileKey()) {
            String fileId = fileIdFromKeyAndFileName(assetFolder.getIdAlias(), fileName);
            if (repo().existsByFileId(fileId)) {
                throw new SingleValidationException(new ValidationError("file", ApiString.FILENAME_NOT_UNIQUE));
            }
            return fileId;
        }

        String fileId;
        int maxTries = 10;
        do {
            final byte[] rand = String.format("%05d", (int) Math.floor(Math.random() * 100000d)).getBytes();
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < rand.length; i++) {
                sb.append(Integer.toString((rand[i] & 0x0f), 16).substring(1));
            }
            fileId = fileIdFromKeyAndFileName(rand.toString().substring(3, 8), fileName);
            maxTries--;
        } while (repo().existsByFileId(fileId) && maxTries >= 0);
        return fileId;
    }

    public String fileIdFromKeyAndFileName(String fileKey, String fileName) {
        return fileKey + "-" + fileName;
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
                logger.warn(MessageFormat.format("Couldn't read duration of audio file \"{0}\".", asset.getFileName()));
            }
        }
    }
}
