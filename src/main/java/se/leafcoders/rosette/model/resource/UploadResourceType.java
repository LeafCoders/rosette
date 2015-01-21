package se.leafcoders.rosette.model.resource;

import org.hibernate.validator.constraints.NotEmpty;
import se.leafcoders.rosette.model.BaseModel;

public class UploadResourceType extends ResourceType {

	@NotEmpty(message = "uploadResourceType.folderName.notEmpty")
    private String folderName;

	private Boolean multiSelect;
	
    // Constructors

    public UploadResourceType() {
		super("user");
    }

    @Override
	public void update(BaseModel updateFrom) {
    	UploadResourceType resourceTypeUpdate = (UploadResourceType) updateFrom;
    	if (resourceTypeUpdate.getFolderName() != null) {
    		setFolderName(resourceTypeUpdate.getFolderName());
    	}
    	if (resourceTypeUpdate.getMultiSelect() != null) {
    		setMultiSelect(resourceTypeUpdate.getMultiSelect());
    	}
    	super.update(updateFrom);
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
