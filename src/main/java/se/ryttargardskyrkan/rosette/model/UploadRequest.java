package se.ryttargardskyrkan.rosette.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

@Document
public class UploadRequest {

	@Id
	private String id;
    @NotNull(message = "upload.fileName.notNull")
	private String fileName;
    @NotNull(message = "upload.mimeType.notNull")
	private String mimeType;
    @NotNull(message = "upload.fileData.notNull")
	private String fileData;

	// Getters and setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	// Helper methods

	@JsonIgnore
	public byte[] getFileDataAsBytes() {
		return DatatypeConverter.parseBase64Binary(fileData);
	}
}
