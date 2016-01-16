package se.leafcoders.rosette.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.reference.UserRef;
import se.leafcoders.rosette.validator.HasRef;
import se.leafcoders.rosette.validator.CheckReference;

@Document(collection = "groupMemberships")
public class GroupMembership extends IdBasedModel {

	@Indexed
    @HasRef(message = "groupMembership.group.mustBeSet")
	@CheckReference
	private Group group;

	@Indexed
    @HasRef(message = "groupMembership.user.mustBeSet")
    @CheckReference(model = User.class)
	private UserRef user;

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		GroupMembership groupMembershipUpdate = (GroupMembership) updateFrom;
    	if (rawData.has("group")) {
    		setGroup(groupMembershipUpdate.getGroup());
    	}
    	if (rawData.has("user")) {
    		setUser(groupMembershipUpdate.getUser());
    	}
	}

	// Getters and setters

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public UserRef getUser() {
        return user;
    }

    public void setUser(UserRef user) {
        this.user = user;
    }
}
