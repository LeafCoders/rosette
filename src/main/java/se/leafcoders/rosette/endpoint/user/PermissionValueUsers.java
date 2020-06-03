package se.leafcoders.rosette.permission;

public class PermissionValueUsers extends PermissionValue {

    private static final String ACTION_ACTIVATE = "activate";
    private static final String ACTION_LOGIN_AS = "loginAs";

    public PermissionValueUsers() {
        super("users");
    }

    public PermissionValueUsers activate() {
        return withAction(ACTION_ACTIVATE);
    }

    public PermissionValueUsers loginAs() {
        return withAction(ACTION_LOGIN_AS);
    }
}
