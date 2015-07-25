package se.leafcoders.rosette.model.upload;

import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UploadResponse extends IdBasedModel {

    @NotNull(message = "uploadResponse.fileName.notNull")
	private String fileName;
    @NotNull(message = "uploadResponse.folderId.notNull")
	private String folderId;
    @NotNull(message = "uploadResponse.fileUrl.notNull")
	private String fileUrl; // Absolute url to downloadable file
    @NotNull(message = "uploadResponse.mimeType.notNull")
	private String mimeType;
	private long fileSize; // Bytes
	private Long width;
	private Long height;

	@Override
	public void update(BaseModel updateFrom) {
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
}
