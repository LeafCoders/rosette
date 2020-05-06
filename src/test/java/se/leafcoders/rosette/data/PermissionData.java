package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.PermissionIn;
import se.leafcoders.rosette.persistence.model.Permission;

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
