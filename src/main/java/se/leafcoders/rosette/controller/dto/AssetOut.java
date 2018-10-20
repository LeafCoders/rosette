package se.leafcoders.rosette.controller.dto;

public class AssetOut {

    private Long id;
    private String type;
    private String mimeType;

    private String fileName;
    private String url;
    
    private boolean isImageFile;
    private boolean isAudioFile;
    private boolean isTextFile;

    private Long fileSize;
    private Long width;
    private Long height;
    private Long duration;
    

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public boolean getIsImageFile() {
        return isImageFile;
    }

    public void setImageFile(boolean isImageFile) {
        this.isImageFile = isImageFile;
    }

    public boolean getIsAudioFile() {
        return isAudioFile;
    }

    public void setAudioFile(boolean isAudioFile) {
        this.isAudioFile = isAudioFile;
    }

    public boolean getIsTextFile() {
        return isTextFile;
    }

    public void setTextFile(boolean isTextFile) {
        this.isTextFile = isTextFile;
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
