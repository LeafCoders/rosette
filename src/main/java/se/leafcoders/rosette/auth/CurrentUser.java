package se.leafcoders.rosette.auth;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

public class CurrentUser extends User {
    private static final long serialVersionUID = -8830680310252157879L;

    @Getter
    private final Long id;
    @Getter
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

    public CurrentUser(Long id, String fullName, String username, String password, Boolean isEnabled,
            String authority) {
        super(username, password, isEnabled, true, true, isEnabled,
                Collections.singleton(new SimpleGrantedAuthority(authority)));
        this.id = id;
        this.fullName = fullName;
    }

    public boolean isSuperAdmin() {
        return getAuthorities() != null
                ? getAuthorities().stream().anyMatch(a -> RosetteAuthority.SUPER_ADMIN.equals(a.getAuthority()))
                : false;
    }
}
