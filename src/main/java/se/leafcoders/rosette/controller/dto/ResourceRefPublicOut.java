package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.util.IdToSlugConverter;

public class ResourceRefPublicOut {

    private static final String RESOURCE_SLUG_PREFIX = "re";
    
    private String slug;
    private String name;

    public ResourceRefPublicOut(Resource resource) {
        this.slug = IdToSlugConverter.convertIdToSlug(resource.getId(), resource.getName(), RESOURCE_SLUG_PREFIX);
        this.name = resource.getName();
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

}
