package se.leafcoders.rosette.controller.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Email(message = ApiString.EMAIL_INVALID)
    private String email;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String firstName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String lastName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String password;
    
    private Boolean isActive;

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}
