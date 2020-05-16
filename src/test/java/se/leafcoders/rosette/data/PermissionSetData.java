package se.leafcoders.rosette.data;

import lombok.NonNull;
import se.leafcoders.rosette.controller.dto.PermissionSetIn;
import se.leafcoders.rosette.persistence.model.PermissionSet;

public class PermissionSetData {

    public static PermissionSet viewAll() {
        PermissionSet permissionSet = new PermissionSet();
        permissionSet.setName("See all views");
        permissionSet.setPatterns("*:view");
        return permissionSet;
    }

    public static PermissionSet readEvents() {
        PermissionSet permissionSet = new PermissionSet();
        permissionSet.setName("Read all events");
        permissionSet.setPatterns("events:read");
        return permissionSet;
    }

    public static PermissionSet ofPatterns(@NonNull String patterns) {
        PermissionSet permissionSet = new PermissionSet();
        permissionSet.setName(patterns);
        permissionSet.setPatterns(patterns);
        return permissionSet;
    }

    public static PermissionSetIn missingAllProperties() {
        return new PermissionSetIn();
    }

    public static PermissionSetIn invalidProperties() {
        PermissionSetIn permissionSet = new PermissionSetIn();
        permissionSet.setName("");
        permissionSet.setPatterns("");
        return permissionSet;
    }

}
