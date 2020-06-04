package se.leafcoders.rosette.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import se.leafcoders.rosette.core.permission.PermissionTree;

public class CurrentUserAuthentication implements Authentication {

    private static final long serialVersionUID = -5506484510955451195L;

    private final CurrentUser user;
    private PermissionTree permissionTree = null;
    private boolean authenticated = true;

    /**
     * Anonymous user authentication
     */
    public CurrentUserAuthentication() {
        this.user = new CurrentUser();
    }

    /**
     * Existing user authentication
     */
    public CurrentUserAuthentication(CurrentUser user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public User getDetails() {
        return user;
    }

    @Override
    public Object getPrincipal() {
        return user.getId();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public PermissionTree getPermissionTree() {
        return permissionTree;
    }

    public void setPermissionTree(PermissionTree permissionTree) {
        this.permissionTree = permissionTree;
    }
}
