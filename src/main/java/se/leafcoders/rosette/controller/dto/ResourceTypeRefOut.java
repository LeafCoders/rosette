package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.ResourceType;

public class ResourceTypeRefOut {

    private Long id;
    private String name;

    public ResourceTypeRefOut(ResourceType resourceType) {
        id = resourceType.getId();
        name = resourceType.getName();
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
