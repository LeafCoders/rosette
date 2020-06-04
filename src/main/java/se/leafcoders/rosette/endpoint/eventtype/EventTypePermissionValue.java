package se.leafcoders.rosette.endpoint.eventtype;

import se.leafcoders.rosette.core.permission.PermissionValue;

public class EventTypePermissionValue extends PermissionValue {

    private final static String ACTION_CREATE_EVENTS = "createEvents";
    private final static String ACTION_READ_EVENTS = "readEvents";
    private final static String ACTION_UPDATE_EVENTS = "updateEvents";
    private final static String ACTION_DELETE_EVENTS = "deleteEvents";
    private final static String ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENTS = "modifyEventResourceRequirements";
    private final static String ACTION_ASSIGN_EVENT_RESOURCES = "assignEventResources";

    public EventTypePermissionValue() {
        super("eventTypes");
    }

    public EventTypePermissionValue createEvents() {
        return withAction(ACTION_CREATE_EVENTS);
    }
    
    public EventTypePermissionValue readEvents() {
        return withAction(ACTION_READ_EVENTS);
    }
    
    public EventTypePermissionValue updateEvents() {
        return withAction(ACTION_UPDATE_EVENTS);
    }
    
    public EventTypePermissionValue deleteEvents() {
        return withAction(ACTION_DELETE_EVENTS);
    }
    
    public EventTypePermissionValue modifyEventResourceRequirements() {
        return withAction(ACTION_MODIFY_EVENT_RESOURCE_REQUIREMENTS);
    }

    public EventTypePermissionValue assignEventResources() {
        return withAction(ACTION_ASSIGN_EVENT_RESOURCES);
    }

}
