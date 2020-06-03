package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.EventTypeIn;
import se.leafcoders.rosette.persistence.model.EventType;

public class EventTypeData {

    public static EventType prayerMeeting() {
        EventType eventType = new EventType();
        eventType.setIdAlias("prayerMeeting");
        eventType.setName("Prayer meeting");
        eventType.setDescription("Any prayer meeting");
        eventType.setIsPublic(true);
        return eventType;
    }

    public static EventType sermon() {
        EventType eventType = new EventType();
        eventType.setIdAlias("sermin");
        eventType.setName("Sermin");
        eventType.setDescription("Sunday sermon");
        eventType.setIsPublic(true);
        return eventType;
    }

    public static EventTypeIn missingAllProperties() {
        return new EventTypeIn();
    }

    public static EventTypeIn invalidProperties() {
        EventTypeIn eventType = new EventTypeIn();
        eventType.setIdAlias("MustNotStartWithUpperCase");
        eventType.setName("");
        eventType.setIsPublic(null);
        return eventType;
    }

    public static EventTypeIn bon() {
        EventTypeIn eventType = new EventTypeIn();
        eventType.setIdAlias("bon");
        eventType.setName("Bön");
        eventType.setIsPublic(true);
        return eventType;
    }

    public static EventTypeIn newEventType(String idAlias, String name) {
        EventTypeIn eventType = new EventTypeIn();
        eventType.setIdAlias(idAlias);
        eventType.setName(name);
        eventType.setIsPublic(true);
        return eventType;
    }

}
