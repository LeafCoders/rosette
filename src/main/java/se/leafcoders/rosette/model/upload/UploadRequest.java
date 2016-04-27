package se.leafcoders.rosette.model.upload;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadRequest extends IdBasedModel {

    @NotNull(message = "upload.fileName.notNull")
    @Pattern(regexp = "[a-zA-Z0-9_.]+", message = "error.fileName.notValidFormat")
	private String fileName;
    @NotNull(message = "upload.mimeType.notNull")
	private String mimeType;
    @NotNull(message = "upload.fileData.notNull")
	private byte[] fileData;
    
	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		UploadRequest uploadRequestUpdate = (UploadRequest) updateFrom;
		if (rawData.has("fileName")) {
			setFileName(uploadRequestUpdate.getFileName());
		}
		if (rawData.has("mimeType")) {
			setMimeType(uploadRequestUpdate.getMimeType());
		}
		if (rawData.has("fileData")) {
			setFileData(uploadRequestUpdate.getFileData());
		}
	}

	// Getters and setters

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

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
}
