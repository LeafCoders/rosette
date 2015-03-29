package se.leafcoders.rosette.model.resource;

import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.upload.UploadFolderRef;

public class UploadResourceType extends ResourceType {

	@NotNull(message = "uploadResourceType.uploadFolder.notEmpty")
    private UploadFolderRef uploadFolder;

	private Boolean multiSelect;
	
    // Constructors

    public UploadResourceType() {
		super("upload");
    }

    @Override
	public void update(BaseModel updateFrom) {
    	UploadResourceType resourceTypeUpdate = (UploadResourceType) updateFrom;
    	if (resourceTypeUpdate.getUploadFolder() != null) {
    		setUploadFolder(resourceTypeUpdate.getUploadFolder());
    	}
    	if (resourceTypeUpdate.getMultiSelect() != null) {
    		setMultiSelect(resourceTypeUpdate.getMultiSelect());
    	}
    	super.update(updateFrom);
    }

    // Getters and setters

	public UploadFolderRef getUploadFolder() {
		return uploadFolder;
	}

	public void setUploadFolder(UploadFolderRef uploadFolder) {
		this.uploadFolder = uploadFolder;
	}

	public Boolean getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(Boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
}
