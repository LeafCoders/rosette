package se.ryttargardskyrkan.rosette.model.resource;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.query.Update;

public class UploadResourceType extends ResourceType {
	@NotEmpty(message = "uploadResourceType.folderName.notEmpty")
    private String folderName;

	private Boolean multiSelect;
	
	@Override
	public Update addToUpdateQuery(Update update) {
		update.set("folderName", folderName);
		update.set("multiSelect", multiSelect);
		return super.addToUpdateQuery(update);
	}
	
    // Getters and setters

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Boolean getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(Boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
}
