package se.leafcoders.rosette.endpoint.resource;

import lombok.Getter;
import se.leafcoders.rosette.util.IdToSlugConverter;

@Getter
public class ResourceRefPublicOut {

    private static final String RESOURCE_SLUG_PREFIX = "re";
    
    private String slug;
    private String name;

    public ResourceRefPublicOut(Resource resource) {
        this.slug = IdToSlugConverter.convertIdToSlug(resource.getId(), resource.getName(), RESOURCE_SLUG_PREFIX);
        this.name = resource.getName();
    }
}
