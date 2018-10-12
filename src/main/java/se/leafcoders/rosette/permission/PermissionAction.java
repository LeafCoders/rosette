package se.leafcoders.rosette.permission;

public enum PermissionAction {
    CREATE("create"), READ("read"), UPDATE("update"), DELETE("delete"), PUBLIC("public");

    private final String action;

    PermissionAction(String action) {
        this.action = action;
    }

    public String toString() {
        return action;
    }

}
