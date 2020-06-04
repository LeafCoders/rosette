package se.leafcoders.rosette.endpoint.auth;

import lombok.Data;

@Data
public class Login {
    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username != null ? username.toLowerCase() : null;
    }
}
