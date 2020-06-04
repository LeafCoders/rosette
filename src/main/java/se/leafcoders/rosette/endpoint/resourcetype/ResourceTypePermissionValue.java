package se.leafcoders.rosette.endpoint.resourcetype;

import se.leafcoders.rosette.core.permission.PermissionValue;

public class ResourceTypePermissionValue extends PermissionValue {

    private final static String ACTION_READ_EVENTS = "readEvents";
    private final static String ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENT = "modifyEventResourceRequirement";
    private final static String ACTION_ASSIGN_EVENT_RESOURCES = "assignEventResources";

    public ResourceTypePermissionValue() {
        super("resourceTypes");
    }

    public ResourceTypePermissionValue readEvents() {
        return withAction(ACTION_READ_EVENTS);
    }

    public ResourceTypePermissionValue modifyEventResourceRequirement() {
        return withAction(ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENT);
    }

    public ResourceTypePermissionValue assignEventResources() {
        return withAction(ACTION_ASSIGN_EVENT_RESOURCES);
    }

}
