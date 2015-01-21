package se.ryttargardskyrkan.rosette.model.resource;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.validator.constraints.NotEmpty;
import se.ryttargardskyrkan.rosette.validator.HasRef;

// The following annotations uses the property 'type' to decide which class to create
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = UserResource.class, name = "user"),
    @JsonSubTypes.Type(value = UploadResource.class, name = "upload")
})
public abstract class Resource {
	@NotEmpty(message = "resource.type.notEmpty")
	private String type;

	@HasRef(message = "error.resourceType.mustBeSet")
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