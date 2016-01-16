package se.leafcoders.rosette.model.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.model.reference.UserRefsAndText;
import se.leafcoders.rosette.validator.CheckReference;

public class UserResource extends Resource {
	@NotNull(message = "userResource.users.notNull")
	@Valid
	@CheckReference(model = User.class, dbKey = "users.refs.id")
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
