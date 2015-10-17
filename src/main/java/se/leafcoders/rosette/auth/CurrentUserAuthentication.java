package se.leafcoders.rosette.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import se.leafcoders.rosette.model.PermissionTree;

public class CurrentUserAuthentication implements Authentication {

    private static final long serialVersionUID = -5506484510955451195L;

    private final CurrentUser user;
    private final PermissionTree permissionTree;
    private boolean authenticated = true;

    public CurrentUserAuthentication(CurrentUser user, PermissionTree permissionTree) {
        this.user = user;
        this.permissionTree = permissionTree;
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
}
