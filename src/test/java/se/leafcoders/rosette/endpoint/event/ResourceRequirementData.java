package se.leafcoders.rosette.endpoint.event;

import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;

public class ResourceRequirementData {

    public static ResourceRequirement create(Event event, ResourceType resourceType) {
        ResourceRequirement resourceRequirement = new ResourceRequirement();
        resourceRequirement.setEvent(event);
        resourceRequirement.setResourceType(resourceType);
        return resourceRequirement;
    }

    public static ResourceRequirementIn newResourceRequirement(Long resourceTypeId) {
        ResourceRequirementIn resourceRequirement = new ResourceRequirementIn();
        resourceRequirement.setResourceTypeId(resourceTypeId);
        return resourceRequirement;
    }
    
}
