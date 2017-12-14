package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.EventType;

public class EventTypeRefOut {

    private Long id;
    private String name;

    public EventTypeRefOut(EventType eventType) {
        id = eventType.getId();
        name = eventType.getName();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
