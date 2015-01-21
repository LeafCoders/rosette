package se.ryttargardskyrkan.rosette.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

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
	public void update(BaseModel updateFrom) {
		UploadRequest uploadRequestUpdate = (UploadRequest) updateFrom;
		if (uploadRequestUpdate.getFileName() != null) {
			setFileName(uploadRequestUpdate.getFileName());
		}
		if (uploadRequestUpdate.getMimeType() != null) {
			setMimeType(uploadRequestUpdate.getMimeType());
		}
		if (uploadRequestUpdate.getFileData() != null) {
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
