package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.ResourceRequirementIn;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.ResourceRequirement;
import se.leafcoders.rosette.persistence.model.ResourceType;

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
