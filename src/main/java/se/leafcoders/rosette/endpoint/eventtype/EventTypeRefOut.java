package se.leafcoders.rosette.endpoint.eventtype;

import lombok.Data;

@Data
public class EventTypeRefOut {

    private Long id;
    private String name;

    public EventTypeRefOut(EventType eventType) {
        id = eventType.getId();
        name = eventType.getName();
    }
}
