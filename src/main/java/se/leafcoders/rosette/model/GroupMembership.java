package se.leafcoders.rosette.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.reference.UserRef;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "groupMemberships")
public class GroupMembership extends IdBasedModel {

	@Indexed
    @HasRef(message = "groupMembership.group.mustBeSet")
	private Group group;

	@Indexed
    @HasRef(message = "groupMembership.user.mustBeSet")
	private UserRef user;

	@Override
	public void update(BaseModel updateFrom) {
		GroupMembership groupMembershipUpdate = (GroupMembership) updateFrom;
    	if (groupMembershipUpdate.getGroup() != null) {
    		setGroup(groupMembershipUpdate.getGroup());
    	}
    	if (groupMembershipUpdate.getUser() != null) {
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
