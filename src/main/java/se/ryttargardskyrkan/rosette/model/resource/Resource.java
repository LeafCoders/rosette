package se.ryttargardskyrkan.rosette.model.resource;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.validator.constraints.NotEmpty;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.validator.HasIdRef;

// The following annotations uses the property 'type' to decide which class to create
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = UserResource.class, name = "user"),
    @JsonSubTypes.Type(value = UploadResource.class, name = "upload")
})
public abstract class Resource {
	@NotEmpty(message = "resource.type.notEmpty")
	private String type;

	@HasIdRef(message = "userResource.resourceType.mustBeSet")
    private ObjectReference<ResourceType> resourceType;
	
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

    public ObjectReference<ResourceType> getResourceType() {
		return resourceType;
	}

	public void setResourceType(ObjectReference<ResourceType> resourceType) {
		this.resourceType = resourceType;
	}
}