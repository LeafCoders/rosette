package se.leafcoders.rosette.controller.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class ResourceRequirementOut {

    private Long id;
    private ResourceTypeRefOut resourceType;
    private Set<ResourceRefOut> resources;

    public Set<ResourceRefOut> getResources() {
        return resources != null ? resources : new HashSet<>();
    }
}
