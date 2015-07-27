package se.leafcoders.rosette.model.upload;

import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadRequest extends IdBasedModel {

    @NotNull(message = "upload.fileName.notNull")
	private String fileName;
    @NotNull(message = "upload.mimeType.notNull")
	private String mimeType;
    @NotNull(message = "upload.fileData.notNull")
	private String fileData;
    
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

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	// Helper methods

	@JsonIgnore
	public byte[] getFileDataAsBytes() {
		int index = fileData.indexOf(",");
		return DatatypeConverter.parseBase64Binary(fileData.substring(index + 1));
	}
}
