package se.leafcoders.rosette.auth;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {
    private static final long serialVersionUID = -8830680310252157879L;

    private String id;

    public CurrentUser(String id, String username, String password) {
        super(username, password, new ArrayList<GrantedAuthority>());
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
