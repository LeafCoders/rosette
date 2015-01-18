package se.ryttargardskyrkan.rosette.model.resource;

import org.springframework.data.mongodb.core.query.Update;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.validator.HasIdRef;

public class UserResourceType extends ResourceType {
	@HasIdRef(message = "userResorceType.group.mustBeSet")
    private ObjectReference<Group> group;

	private Boolean multiSelect;
	private Boolean allowText;

    // Constructors

    public UserResourceType() {
		super("user");
    }

    @Override
	public Update addToUpdateQuery(Update update) {
		update.set("group", group);
		update.set("multiSelect", multiSelect);
		update.set("allowText", allowText);
		return super.addToUpdateQuery(update);
	}
	
    // Getters and setters

    public ObjectReference<Group> getGroup() {
        return group;
    }

    public void setGroup(ObjectReference<Group> group) {
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
