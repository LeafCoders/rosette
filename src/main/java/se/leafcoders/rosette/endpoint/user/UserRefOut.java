package se.leafcoders.rosette.endpoint.user;

import lombok.Data;

@Data
public class UserRefOut {

    private Long id;
    private String firstName;
    private String lastName;

    public UserRefOut(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
    }
}
