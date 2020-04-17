package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Email(message = ApiString.EMAIL_INVALID)
    private String email;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String firstName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String lastName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String password;
    
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    private Boolean isActive;

    public UserIn() {}
    
    public UserIn(SignupUserIn signupUser) {
        isActive = null;
        email = signupUser.getEmail();
        firstName = signupUser.getFirstName();
        lastName = signupUser.getLastName();
        password = signupUser.getPassword();
        description = signupUser.getDescription();
    }
}
