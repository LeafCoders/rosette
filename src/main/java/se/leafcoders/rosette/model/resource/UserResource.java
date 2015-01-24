package se.leafcoders.rosette.model.resource;

import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.reference.UserRefsAndText;

public class UserResource extends Resource {
	@NotNull(message = "userResource.users.notNull")
    private UserRefsAndText users;

    // Constructors

    public UserResource() {
		super("user");
    }

    // Getters and setters

    public UserRefsAndText getUsers() {
        return users;
    }

    public void setUsers(UserRefsAndText users) {
        this.users = users;
    }
}
