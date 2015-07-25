package se.leafcoders.rosette.model.resource;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.TypeBasedModel;

@Document(collection = "resourceTypes")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = UserResourceType.class, name = "user"),
    @JsonSubTypes.Type(value = UploadResourceType.class, name = "upload")
})
public abstract class ResourceType extends TypeBasedModel {
	@NotEmpty(message = "resourceType.type.notEmpty")
    protected String type;

	@NotEmpty(message = "resourceType.section.notEmpty")
	private String section;

    // Constructors

    public ResourceType(String type) {
    	this.type = type;
    }

    @Override
	public void update(BaseModel updateFrom) {
    	ResourceType resourceTypeUpdate = (ResourceType) updateFrom;
    	if (resourceTypeUpdate.getSection() != null) {
    		setSection(resourceTypeUpdate.getSection());
    	}
    	super.update(updateFrom);
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

}
