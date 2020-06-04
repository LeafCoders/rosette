package se.leafcoders.rosette.endpoint.resourcetype;

import lombok.Data;

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
