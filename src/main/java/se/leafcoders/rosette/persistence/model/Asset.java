package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "assets")
public class Asset extends Persistable {

    public enum AssetType { FILE, URL };

    @NotNull(message = ApiString.STRING_NOT_EMPTY)
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private AssetType type;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String mimeType;

    @NotNull(message = ApiString.NOT_NULL)
    private Long folderId;
    
    private String fileId;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Pattern(regexp = "^[\\w._-]+.[\\w]$", message = ApiString.FILENAME_INVALID)
    private String fileName;    // File name in server file system

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @URL
    private String url;         // URL to external asset 

    private Long fileSize;      // (bytes)
    private Long width;         // Image
    private Long height;        // Image
    private Long duration;      // Audio (milliseconds)


    public Asset() {}


    // Getters and setters


    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long length) {
        this.duration = length;
    }

}
