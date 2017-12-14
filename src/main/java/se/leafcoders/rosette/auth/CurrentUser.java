package se.leafcoders.rosette.auth;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {
    private static final long serialVersionUID = -8830680310252157879L;

    private final Long id;
    private final String fullName;

    public CurrentUser() {
        super("anonymous", "anonymous", new ArrayList<GrantedAuthority>());
        this.id = null;
        this.fullName = null;
    }

    public CurrentUser(Long id, String fullName, String username, String password, Boolean isEnabled) {
        super(username, password, isEnabled, true, true, isEnabled, new ArrayList<GrantedAuthority>());
        this.id = id;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }
}
