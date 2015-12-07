package se.leafcoders.rosette.model.upload;

import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@Document
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UploadResponse extends IdBasedModel {

	private String fileName;
	private String folderId;
	private String fileUrl; // Absolute url to downloadable file
	private String mimeType;
	private long fileSize; // Bytes
	private Long width; // Image
	private Long height; // Image
    private Long duration; // Audio

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
	}

	// Getters and setters

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
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
