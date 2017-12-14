package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.EventTypeIn;
import se.leafcoders.rosette.persistence.model.EventType;

public class EventTypeData {

    public static EventType prayerMeeting() {
        EventType eventType = new EventType();
        eventType.setIdAlias("prayerMeeting");
        eventType.setName("Prayer meeting");
        eventType.setDescription("Any prayer meeting");
        return eventType;
    }

    public static EventType sermon() {
        EventType eventType = new EventType();
        eventType.setIdAlias("sermin");
        eventType.setName("Sermin");
        eventType.setDescription("Sunday sermon");
        return eventType;
    }

    public static EventTypeIn missingAllProperties() {
        return new EventTypeIn();
    }

    public static EventTypeIn invalidProperties() {
        EventTypeIn eventType = new EventTypeIn();
        eventType.setIdAlias("MustNotStartWithUpperCase");
        eventType.setName("");
        return eventType;
    }

    public static EventTypeIn bon() {
        EventTypeIn eventType = new EventTypeIn();
        eventType.setIdAlias("bon");
        eventType.setName("Bön");
        return eventType;
    }

    public static EventTypeIn newEventType(String idAlias, String name) {
        EventTypeIn eventType = new EventTypeIn();
        eventType.setIdAlias(idAlias);
        eventType.setName(name);
        return eventType;
    }

}
