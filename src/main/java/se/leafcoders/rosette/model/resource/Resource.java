package se.leafcoders.rosette.model.resource;

import org.hibernate.validator.constraints.NotEmpty;
import se.leafcoders.rosette.validator.HasRef;
import se.leafcoders.rosette.validator.CheckReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// The following annotations uses the property 'type' to decide which class to create
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = UserResource.class, name = "user"),
    @JsonSubTypes.Type(value = UploadResource.class, name = "upload")
})
public abstract class Resource {
	@NotEmpty(message = "resource.type.notEmpty")
	private String type;

	@HasRef(message = "error.resourceType.mustBeSet")
    @CheckReference
    private ResourceType resourceType;
	
    // Constructors

    public Resource() {}

	public Resource(String type) {
		this.type = type;
	}

    // Getters and setters

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
}