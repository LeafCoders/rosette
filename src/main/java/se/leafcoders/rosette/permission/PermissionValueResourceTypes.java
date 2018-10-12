package se.leafcoders.rosette.permission;

public class PermissionValueResourceTypes extends PermissionValue {

    private final static String ACTION_READ_EVENTS = "readEvents";
    private final static String ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENT = "modifyEventResourceRequirement";
    private final static String ACTION_ASSIGN_EVENT_RESOURCES = "assignEventResources";

    public PermissionValueResourceTypes() {
        super("resourceTypes");
    }

    public PermissionValueResourceTypes readEvents() {
        return withAction(ACTION_READ_EVENTS);
    }
    
    public PermissionValueResourceTypes modifyEventResourceRequirement() {
        return withAction(ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENT);
    }
    
    public PermissionValueResourceTypes assignEventResources() {
        return withAction(ACTION_ASSIGN_EVENT_RESOURCES);
    }
    
}
