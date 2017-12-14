package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.ResourceTypeIn;
import se.leafcoders.rosette.persistence.model.ResourceType;

public class ResourceTypeData {

    public static ResourceType sound() {
        ResourceType resourceType = new ResourceType();
        resourceType.setIdAlias("sound");
        resourceType.setName("Sound");
        resourceType.setDescription("Sound description");
        return resourceType;
    }

    public static ResourceType preacher() {
        ResourceType resourceType = new ResourceType();
        resourceType.setIdAlias("preacher");
        resourceType.setName("Preacher");
        resourceType.setDescription("Preacher description");
        return resourceType;
    }

    public static ResourceTypeIn missingAllProperties() {
        return new ResourceTypeIn();
    }

    public static ResourceTypeIn invalidProperties() {
        ResourceTypeIn resourceType = new ResourceTypeIn();
        resourceType.setIdAlias("MustNotStartWithUpperCase");
        resourceType.setName("");
        return resourceType;
    }

    public static ResourceTypeIn newResourceType() {
        return ResourceTypeData.newResourceType("idResourceType", "Resource type");
    }
    
    public static ResourceTypeIn newResourceType(String idAlias, String name) {
        ResourceTypeIn resourceType = new ResourceTypeIn();
        resourceType.setIdAlias(idAlias);
        resourceType.setName(name);
        return resourceType;
    }

}
