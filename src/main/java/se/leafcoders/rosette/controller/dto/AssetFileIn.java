package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetFileIn {

    private MultipartFile file;
    private Long folderId;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Pattern(regexp = "^[\\w._-]+.[\\w]$", message = ApiString.FILENAME_INVALID)
    private String fileName;

    // Getters and setters

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
