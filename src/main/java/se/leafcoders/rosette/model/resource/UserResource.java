package se.leafcoders.rosette.model.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.reference.UserRefsAndText;

public class UserResource extends Resource {
	@NotNull(message = "userResource.users.notNull")
	@Valid
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
