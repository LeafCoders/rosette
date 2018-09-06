package se.leafcoders.rosette.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "forgottenpassword")
public class ForgottenPassword extends Persistable {

    @NotNull(message = ApiString.NOT_NULL)
    private Long userId;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String token;

    public ForgottenPassword() {
    }

    // Getters and setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
