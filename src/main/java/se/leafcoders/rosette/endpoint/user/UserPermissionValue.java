package se.leafcoders.rosette.endpoint.user;

import se.leafcoders.rosette.core.permission.PermissionValue;

public class UserPermissionValue extends PermissionValue {

    private static final String ACTION_ACTIVATE = "activate";
    private static final String ACTION_LOGIN_AS = "loginAs";

    public UserPermissionValue() {
        super("users");
    }

    public UserPermissionValue activate() {
        return withAction(ACTION_ACTIVATE);
    }

    public UserPermissionValue loginAs() {
        return withAction(ACTION_LOGIN_AS);
    }
}
