package se.leafcoders.rosette.model.upload;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.TypeBasedModel;

@Document(collection = "uploadFolders")
public class UploadFolder extends TypeBasedModel {

	private Boolean isPublic = false;

	@NotEmpty(message = "uploadFolder.mimeTypes.notEmpty")
	private List<String> mimeTypes;

	@Override
	public void update(BaseModel updateFrom) {
		UploadFolder uploadFolderUpdate = (UploadFolder) updateFrom;
		if (uploadFolderUpdate.getIsPublic() != null) {
			setIsPublic(uploadFolderUpdate.getIsPublic());
		}
		if (uploadFolderUpdate.getMimeTypes() != null) {
			setMimeTypes(uploadFolderUpdate.getMimeTypes());
		}
		super.update(uploadFolderUpdate);
	}

	// Getters and setters

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<String> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(List<String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
}
