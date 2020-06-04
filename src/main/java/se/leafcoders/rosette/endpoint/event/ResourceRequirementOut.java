package se.leafcoders.rosette.endpoint.event;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import se.leafcoders.rosette.endpoint.resource.ResourceRefOut;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRefOut;

@Data
public class ResourceRequirementOut {

    private Long id;
    private ResourceTypeRefOut resourceType;
    private Set<ResourceRefOut> resources;

    public Set<ResourceRefOut> getResources() {
        return resources != null ? resources : new HashSet<>();
    }
}
