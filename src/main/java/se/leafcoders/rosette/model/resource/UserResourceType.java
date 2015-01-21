package se.leafcoders.rosette.model.resource;

import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.validator.HasRef;

public class UserResourceType extends ResourceType {
	@HasRef
    private Group group;

	private Boolean multiSelect;
	private Boolean allowText;

    // Constructors

    public UserResourceType() {
		super("user");
    }

    @Override
	public void update(BaseModel updateFrom) {
    	UserResourceType resourceTypeUpdate = (UserResourceType) updateFrom;
    	if (resourceTypeUpdate.getGroup() != null) {
    		setGroup(resourceTypeUpdate.getGroup());
    	}
    	if (resourceTypeUpdate.getMultiSelect() != null) {
    		setMultiSelect(resourceTypeUpdate.getMultiSelect());
    	}
    	if (resourceTypeUpdate.getAllowText() != null) {
    		setAllowText(resourceTypeUpdate.getAllowText());
    	}
    	super.update(updateFrom);
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
