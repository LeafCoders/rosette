package se.leafcoders.rosette.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class ResourceRequirementOut {

    private Long id;
    private ResourceTypeRefOut resourceType;
    private List<ResourceRefOut> resources;
    
    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceTypeRefOut getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceTypeRefOut resourceType) {
        this.resourceType = resourceType;
    }

    public List<ResourceRefOut> getResources() {
        return resources != null ? resources : new ArrayList<>();
    }

    public void setResources(List<ResourceRefOut> resources) {
        this.resources = resources;
    }

}
