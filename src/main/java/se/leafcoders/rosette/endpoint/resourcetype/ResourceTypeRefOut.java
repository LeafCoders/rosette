package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.ResourceType;

@Data
public class ResourceTypeRefOut {

    private Long id;
    private String name;
    private Long displayOrder;

    public ResourceTypeRefOut(ResourceType resourceType) {
        id = resourceType.getId();
        name = resourceType.getName();
        displayOrder = resourceType.getDisplayOrder();
    }
}
