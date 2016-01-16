package se.leafcoders.rosette.model.resource;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.upload.UploadFolder;
import se.leafcoders.rosette.model.upload.UploadFolderRef;
import se.leafcoders.rosette.validator.CheckReference;

public class UploadResourceType extends ResourceType {

	@NotNull(message = "uploadResourceType.uploadFolder.notEmpty")
    @CheckReference(model = UploadFolder.class)
    private UploadFolderRef uploadFolder;

	private Boolean multiSelect;
	
    // Constructors

    public UploadResourceType() {
		super("upload");
    }

    @Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
    	UploadResourceType resourceTypeUpdate = (UploadResourceType) updateFrom;
    	if (rawData.has("uploadFolder")) {
    		setUploadFolder(resourceTypeUpdate.getUploadFolder());
    	}
    	if (rawData.has("multiSelect")) {
    		setMultiSelect(resourceTypeUpdate.getMultiSelect());
    	}
    	super.update(rawData, updateFrom);
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
