package se.leafcoders.rosette.model.resource;

import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.validator.HasRef;
import se.leafcoders.rosette.validator.CheckReference;

public class UserResourceType extends ResourceType {
	@HasRef(message = "userResourceType.group.mustBeSet")
    @CheckReference
    private Group group;

	private Boolean multiSelect;
	private Boolean allowText;

    // Constructors

    public UserResourceType() {
		super("user");
    }

    @Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
    	UserResourceType resourceTypeUpdate = (UserResourceType) updateFrom;
    	if (rawData.has("group")) {
    		setGroup(resourceTypeUpdate.getGroup());
    	}
    	if (rawData.has("multiSelect")) {
    		setMultiSelect(resourceTypeUpdate.getMultiSelect());
    	}
    	if (rawData.has("allowText")) {
    		setAllowText(resourceTypeUpdate.getAllowText());
    	}
    	super.update(rawData, updateFrom);
    }

    // Getters and setters

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

	public Boolean getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(Boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public Boolean getAllowText() {
		return allowText;
	}

	public void setAllowText(Boolean allowText) {
		this.allowText = allowText;
	}
}
