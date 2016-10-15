package se.leafcoders.rosette.service;

import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.io.Files;
import se.leafcoders.rosette.config.RosetteSettings;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.upload.UploadFile;
import se.leafcoders.rosette.util.FileByteRangeSupport;

@Service
public class FileStorageService {

    static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Autowired
    private RosetteSettings rosetteSettings;

    @PostConstruct
    public void initialize() {
        if (!folderExist(null, null)) {
            if (!createFolder(null, null)) {
                logger.error("Setting 'rosette.uploadsPath' specifies a folder without write access.");
                System.exit(0);
            }
        }
    }

    public boolean folderExist(String folderName, String subFolderName) {
        String folderPath = absolutePath(folderName, subFolderName);
        boolean result = false;
        try {
            result = new File(folderPath).exists();
        } catch (SecurityException e) {
            logger.error("Failed to check if folder " + folderPath + " exists. " + e.getMessage());
        }
        return result;
    }

    public boolean createFolder(String folderName, String subFolderName) {
        String filePath = absolutePath(folderName, subFolderName);
        boolean result = false;
        try {
            result = new File(filePath).mkdir();
        } catch (SecurityException e) {
            logger.error("Failed to create folder " + filePath + ". " + e.getMessage());
        }
        return result;
    }

    public boolean fileExist(String folderName, String subFolderName, String fileName) {
        String filePath = absolutePath(folderName, subFolderName, fileName);
        boolean result = false;
        try {
            result = new File(filePath).exists();
        } catch (SecurityException e) {
            logger.error("Failed to check if file " + filePath + " exists. ", e.getMessage());
        }
        return result;
    }

    public boolean storeFile(String folderName, String subFolderName, String fileName, byte[] fileData) {
        String filePath = absolutePath(folderName, subFolderName, fileName);
        try {
            Files.write(fileData, new File(filePath));
        } catch (IOException e) {
            logger.error("Failed to write file to " + filePath + ". " + e.getMessage());
            return false;
        }
        return true;
    }

    public void streamFile(String thumbSize, UploadFile file, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String fileName = file.getFileName();
        // Indicate the browser to view the file
        response.addHeader("Content-Disposition", "inline; " + fileName);

        if (fileName != null) {
            try {
                response.addHeader("Cache-Control", "public");
                if (rosetteSettings.getUploadCacheMaxAge() > 0) {
                    response.addHeader("Cache-Control", "max-age=" + rosetteSettings.getUploadCacheMaxAge());
                }
                String filePath = absolutePath(file.getFolderId(), thumbSize, fileName);
                Long fileSize = file.getFileSize();
                if (thumbSize != null) {
                    // Size of thumb is not stored in database. Must read from file.
                    fileSize = new File(filePath).length();
                }
                new FileByteRangeSupport().with(request).with(response).serveResource(filePath, fileName, fileSize, file.getMimeType());
            } catch (ClientAbortException ignore) {
                return;
            }
        } else {
            throw new NotFoundException("Upload", fileName);
        }
    }

    public String absolutePath(String... parts) {
        String path = rosetteSettings.getUploadsPath();
        for (String part : parts) {
            if (part != null) {
                path += "/" + part;
            }
        }
        return path;
    }
}
