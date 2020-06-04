package se.leafcoders.rosette.endpoint.event;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import se.leafcoders.rosette.endpoint.eventtype.EventType;
import se.leafcoders.rosette.test.TimeRange;

public class EventData {

    public static Event existingEvent(EventType eventType, @NonNull String utcStartTime, long durationMinutes) {
        Event event = new Event();
        event.setStartTime(LocalDateTime.parse(utcStartTime));
        event.setEndTime(LocalDateTime.parse(utcStartTime).plusMinutes(durationMinutes));
        event.setTitle("Event title");
        event.setDescription("An event");
        event.setEventType(eventType);
        event.setIsPublic(true);
        return event;
    }

    public static Event existingEvent(EventType eventType) {
        return existingEvent(eventType,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), 60);
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

    public static Map<String, Object> newEvent(long eventTypeId, @NonNull String startTime, @NonNull String endTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("eventTypId", eventTypeId);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("title", "Event title");
        data.put("description", "An event");
        return data;
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
