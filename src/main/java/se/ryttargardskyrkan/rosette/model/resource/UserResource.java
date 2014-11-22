package se.ryttargardskyrkan.rosette.model.resource;

import javax.validation.constraints.NotNull;
import se.ryttargardskyrkan.rosette.model.ObjectReferencesAndText;
import se.ryttargardskyrkan.rosette.model.User;

public class UserResource extends Resource {
	@NotNull(message = "userResource.users.notNull")
    private ObjectReferencesAndText<User> users;

    // Constructors

    public UserResource() {
		super("user");
    }
	
    // Getters and setters

    public ObjectReferencesAndText<User> getUsers() {
        return users;
    }

    public void setUsers(ObjectReferencesAndText<User> users) {
        this.users = users;
    }
}
