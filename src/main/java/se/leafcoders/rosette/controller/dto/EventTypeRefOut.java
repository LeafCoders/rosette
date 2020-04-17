package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.EventType;

@Data
public class EventTypeRefOut {

    private Long id;
    private String name;

    public EventTypeRefOut(EventType eventType) {
        id = eventType.getId();
        name = eventType.getName();
    }
}
