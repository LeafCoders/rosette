package se.leafcoders.rosette.controller.dto;

import java.util.HashSet;
import java.util.Set;

public class ResourceRequirementOut {

    private Long id;
    private ResourceTypeRefOut resourceType;
    private Set<ResourceRefOut> resources;
    
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

    public Set<ResourceRefOut> getResources() {
        return resources != null ? resources : new HashSet<>();
    }

    public void setResources(Set<ResourceRefOut> resources) {
        this.resources = resources;
    }

}
