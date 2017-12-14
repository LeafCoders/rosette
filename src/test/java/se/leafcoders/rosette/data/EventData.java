package se.leafcoders.rosette.data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import se.leafcoders.rosette.TimeRange;
import se.leafcoders.rosette.controller.dto.EventIn;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.EventType;

public class EventData {

    public static Event existingEvent(EventType eventType) {
        Event event = new Event();
        event.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        event.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusHours(2));
        event.setTitle("Event title");
        event.setDescription("An event");
        event.setEventType(eventType);
        return event;
    }

    public static EventIn missingAllProperties() {
        return new EventIn();
    }

    public static EventIn invalidProperties() {
        EventIn event = new EventIn();
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().minusHours(2));
        event.setTitle("");
        event.setEventTypeId(null);
        return event;
    }

    public static EventIn newEvent(EventType eventType) {
        EventIn event = new EventIn();
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusHours(2));
        event.setTitle("Event title");
        event.setDescription("An event");
        event.setEventTypeId(eventType.getId());
        return event;
    }
    
    public static EventIn newEvent(Long eventTypeId, String title, TimeRange timeRange) {
        EventIn event = new EventIn();
        event.setStartTime(timeRange.getStart());
        event.setEndTime(timeRange.getEnd());
        event.setTitle(title);
        event.setDescription("Ett event med titelen " + title);
        event.setEventTypeId(eventTypeId);
        return event;
    }

}
