package se.leafcoders.rosette.endpoint.permission;

import lombok.NonNull;

public class PermissionData {

    public static Permission manageEvents() {
        Permission permission = new Permission();
        permission.setName("Manage events");
        permission.setLevel(Permission.LEVEL_ALL_USERS);
        permission.setPatterns("events:*");
        return permission;
    }

    public static Permission manageUsers(Long userId) {
        Permission permission = new Permission();
        permission.setName("Manage users");
        permission.setLevel(Permission.LEVEL_USER);
        permission.setEntityId(userId);
        permission.setPatterns("users:*");
        return permission;
    }

    public static PermissionIn manageGroups() {
        PermissionIn permission = new PermissionIn();
        permission.setName("Manage users");
        permission.setLevel(Permission.LEVEL_ALL_USERS);
        permission.setPatterns("groups:*");
        return permission;
    }

    public static Permission forUser(@NonNull Long userId, @NonNull String patterns) {
        Permission permission = new Permission();
        permission.setName(patterns);
        permission.setLevel(Permission.LEVEL_USER);
        permission.setEntityId(userId);
        permission.setPatterns(patterns);
        return permission;
    }

    public static Permission forGroup(@NonNull Long groupId, @NonNull String patterns) {
        Permission permission = new Permission();
        permission.setName(patterns);
        permission.setLevel(Permission.LEVEL_GROUP);
        permission.setEntityId(groupId);
        permission.setPatterns(patterns);
        return permission;
    }

    public static Permission forAllUsers(@NonNull String patterns) {
        Permission permission = new Permission();
        permission.setName(patterns);
        permission.setLevel(Permission.LEVEL_ALL_USERS);
        permission.setPatterns(patterns);
        return permission;
    }

    public static PermissionIn missingAllProperties() {
        return new PermissionIn();
    }

    public static PermissionIn invalidProperties() {
        PermissionIn permission = new PermissionIn();
        permission.setName("");
        permission.setLevel(4711);
        permission.setEntityId(-1L);
        permission.setPatterns("");
        return permission;
    }
}
