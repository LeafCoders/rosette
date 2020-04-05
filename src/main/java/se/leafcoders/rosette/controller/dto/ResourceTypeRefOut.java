package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.ResourceType;

public class ResourceTypeRefOut {

    private Long id;
    private String name;
    private Long displayOrder;

    public ResourceTypeRefOut(ResourceType resourceType) {
        id = resourceType.getId();
        name = resourceType.getName();
        displayOrder = resourceType.getDisplayOrder();
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

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

}
