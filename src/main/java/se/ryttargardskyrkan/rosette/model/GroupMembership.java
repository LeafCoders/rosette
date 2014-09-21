package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.validator.HasIdRef;

@Document(collection = "groupMemberships")
public class GroupMembership extends IdBasedModel {

    @Indexed
    @HasIdRef(message = "groupMembership.group.mustBeSet")
	private ObjectReference<Group> group;

    @Indexed
    @HasIdRef(message = "groupMembership.user.mustBeSet")
	private ObjectReference<User> user;

    // Getters and setters

    public ObjectReference<Group> getGroup() {
        return group;
    }

    public void setGroup(ObjectReference<Group> group) {
        this.group = group;
    }

    public ObjectReference<User> getUser() {
        return user;
    }

    public void setUser(ObjectReference<User> user) {
        this.user = user;
    }
}
