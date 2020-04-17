package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.User;

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
