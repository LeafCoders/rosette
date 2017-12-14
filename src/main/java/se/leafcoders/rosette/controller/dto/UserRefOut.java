package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.User;

public class UserRefOut {

    private Long id;
    private String firstName;
    private String lastName;

    public UserRefOut(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
