package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.ResourceIn;
import se.leafcoders.rosette.persistence.model.Resource;

public class ResourceData {

    public static Resource lasseLjudtekniker() {
        Resource resource = new Resource();
        resource.setName("Lasse Ljudtekniker");
        resource.setDescription("Ljudtekniker 1");
        return resource;
    }

    public static Resource loffeLjudtekniker() {
        Resource resource = new Resource();
        resource.setName("Loffe Ljudtekniker");
        resource.setDescription("Ljudtekniker 2");
        return resource;
    }

    public static ResourceIn missingAllProperties() {
        return new ResourceIn();
    }

    public static ResourceIn invalidProperties() {
        ResourceIn resource = new ResourceIn();
        resource.setName("");
        return resource;
    }

    public static ResourceIn newResource(String name, Long userId) {
        ResourceIn resource = new ResourceIn();
        resource.setName(name);
        resource.setUserId(userId);
        return resource;
    }
}
