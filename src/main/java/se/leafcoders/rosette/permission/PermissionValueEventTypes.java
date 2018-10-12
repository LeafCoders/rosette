package se.leafcoders.rosette.permission;

public class PermissionValueEventTypes extends PermissionValue {

    private final static String ACTION_CREATE_EVENTS = "createEvents";
    private final static String ACTION_READ_EVENTS = "readEvents";
    private final static String ACTION_UPDATE_EVENTS = "updateEvents";
    private final static String ACTION_DELETE_EVENTS = "deleteEvents";
    private final static String ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENTS = "modifyEventResourceRequirements";
    private final static String ACTION_ASSIGN_EVENT_RESOURCES = "assignEventResources";

    public PermissionValueEventTypes() {
        super("eventTypes");
    }

    public PermissionValueEventTypes createEvents() {
        return withAction(ACTION_CREATE_EVENTS);
    }
    
    public PermissionValueEventTypes readEvents() {
        return withAction(ACTION_READ_EVENTS);
    }
    
    public PermissionValueEventTypes updateEvents() {
        return withAction(ACTION_UPDATE_EVENTS);
    }
    
    public PermissionValueEventTypes deleteEvents() {
        return withAction(ACTION_DELETE_EVENTS);
    }
    
    public PermissionValueEventTypes modifyEventResourceRequirements() {
        return withAction(ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENTS);
    }

    public PermissionValueEventTypes assignEventResources() {
        return withAction(ACTION_ASSIGN_EVENT_RESOURCES);
    }

}
