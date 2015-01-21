package se.ryttargardskyrkan.rosette.model;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Pattern;

@Document(collection = "uploadFolders")
public class UploadFolder extends IdBasedModel {

	@NotEmpty(message = "uploadFolder.title.notEmpty")
	private String title;

	// Name will be used as part of the upload url, so it must only contain valid url chars
	@NotEmpty(message = "uploadFolder.name.notEmpty")
	@Pattern(regexp="[a-z]")
	private String name;

	@NotEmpty(message = "uploadFolder.isPublic.notEmpty")
	private Boolean isPublic = false;

	@NotEmpty(message = "uploadFolder.mimeTypes.notEmpty")
	private List<String> mimeTypes;

	@Override
	public void update(BaseModel updateFrom) {
		UploadFolder uploadFolderUpdate = (UploadFolder) updateFrom;
		if (uploadFolderUpdate.getTitle() != null) {
			setTitle(uploadFolderUpdate.getTitle());
		}
		if (uploadFolderUpdate.getName() != null) {
			setName(uploadFolderUpdate.getName());
		}
		if (uploadFolderUpdate.getIsPublic() != null) {
			setIsPublic(uploadFolderUpdate.getIsPublic());
		}
		if (uploadFolderUpdate.getMimeTypes() != null) {
			setMimeTypes(uploadFolderUpdate.getMimeTypes());
		}
	}

	// Getters and setters

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
