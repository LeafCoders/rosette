package se.leafcoders.rosette.persistence.service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.io.Files;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import se.leafcoders.rosette.RosetteSettings;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Asset.AssetType;
import se.leafcoders.rosette.util.FileByteRangeSupport;

@Service
public class FileStorageService {

    static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final int THUMB_WIDTH_ICON = 300;
    private final int THUMB_HEIGHT_ICON = 300;

    @Autowired
    private RosetteSettings rosetteSettings;

    @PostConstruct
    public void initialize() {
        final Long rootFolder = null;
        if (!folderExist(rootFolder)) {
            if (!createFolder(rootFolder)) {
                logger.error(MessageFormat.format("Setting \"rosette.filesPath\" specifies a folder without write access. The root path is \"{0}\".", absolutePath(null)));
                System.exit(0);
            }
        }
    }

    public boolean folderExist(Long folderId) {
        return folderExist(absolutePath(folderId));
    }

    private boolean folderExist(String folderPath) {
        boolean result = false;
        try {
            result = new File(folderPath).exists();
        } catch (SecurityException e) {
            logger.error(MessageFormat.format("Failed to check if folder \"{0}\" exists. Reason: {1}", folderPath, e.getMessage()), e);
        }
        return result;
    }

    public boolean createFolder(Long folderId) {
        return createFolder(absolutePath(folderId));
    }

    public boolean createFolder(String folderPath) {
        boolean result = false;
        try {
            result = new File(folderPath).mkdir();
        } catch (SecurityException e) {
            logger.error(MessageFormat.format("Failed to create folder \"{0}\". Reason: {1}", folderPath, e.getMessage()), e);
        }
        return result;
    }

    public boolean fileExist(Long folderId, String fileName) {
        String filePath = absolutePath(folderId, fileName);
        boolean result = false;
        try {
            result = new File(filePath).exists();
        } catch (SecurityException e) {
            logger.error(MessageFormat.format("Failed to check if file \"{0}\" exists. Reason: {1}", filePath, e.getMessage()), e);
        }
        return result;
    }

    public boolean storeFile(Long folderId, String fileName, byte[] fileData) {
        String filePath = absolutePath(folderId, fileName);
        try {
            Files.write(fileData, new File(filePath));
        } catch (IOException e) {
            logger.error(MessageFormat.format("Failed to write file to \"{0}\". Reason: {1}", filePath, e.getMessage()), e);
            return false;
        }
        return true;
    }

    public void streamAssetFile(String thumbSize, Asset asset, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String fileName = asset.fileNameOnDisk();

        if (fileName != null) {
            try {
                final String filePath;
                final Long fileSize;
                if (thumbSize != null) {
                    filePath = getThumbSizeFilePath(asset, thumbSize);
                    // Size of thumb is not stored in database. Must read from file.
                    fileSize = new File(filePath).length();
                } else {
                    filePath = absolutePath(asset.getFolderId(), thumbSize, fileName);
                    fileSize = asset.getFileSize();
                }
                Long expiresIn = null;
                // No cache for text files
                if (!asset.isTextFile() && rosetteSettings.getFileClientCacheMaxAge() > 0) {
                    expiresIn = rosetteSettings.getFileClientCacheMaxAge();
                }
                String uniqueName = asset.getId().toString() + "_" + fileName;
                new FileByteRangeSupport().with(request).with(response).serveResource(filePath, uniqueName, fileSize, asset.getMimeType(), expiresIn);
            } catch (ClientAbortException ignore) {
                return;
            }
        } else {
            throw new NotFoundException(fileName);
        }
    }

    private String getThumbSizeFilePath(Asset ofAsset, final String thumbSize) throws Exception {
        String thumbFolderPath = absolutePath(ofAsset.getFolderId(), thumbSize);
        if (!folderExist(thumbFolderPath)) {
            if (!createFolder(thumbFolderPath)) {
                logger.error(MessageFormat.format("Folder \"{0}\" couldn't be created.", thumbFolderPath));
                // TODO: Better please!
                throw new NotFoundException("Folder could not be created!");
            }
        }
        return createThumbSize(ofAsset, thumbSize);
    }
    
    private String createThumbSize(Asset ofAsset, final String thumbSize) throws IOException {
        if (ofAsset.getType() == AssetType.FILE && ofAsset.getMimeType().startsWith("image")) {
            String orgFilePath = absolutePath(ofAsset.getFolderId(), null, ofAsset.fileNameOnDisk());
            String thumbFilePath = absolutePath(ofAsset.getFolderId(), thumbSize, ofAsset.fileNameOnDisk());
            Thumbnails.of(orgFilePath)
                .crop(Positions.CENTER)
                .size(THUMB_WIDTH_ICON, THUMB_HEIGHT_ICON)
                .toFile(thumbFilePath);
            return thumbFilePath;
        } else {
    		    // TODO: Should return a icon image for the mime type of this asset
    		    return ofAsset.fileNameOnDisk();
        }
    }

    public String absolutePath(Long folderId, String... parts) {
        String path = absolutePath(folderId);
        for (String part : parts) {
            if (part != null) {
                path += "/" + part;
            }
        }
        return path;
        
    }

    public String absolutePath(Long folderId) {
        return rosetteSettings.getFilesPath() + (folderId != null ? ("/f_" + String.format("%05d", folderId)) : "");
    }
}
