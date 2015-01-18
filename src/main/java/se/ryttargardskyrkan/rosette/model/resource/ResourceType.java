package se.ryttargardskyrkan.rosette.model.resource;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import se.ryttargardskyrkan.rosette.model.TypeBasedModel;

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
	
	public Update addToUpdateQuery(Update update) {
		update.set("section", section);
		update.set("name", name);
		update.set("description", description);
		return update;
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
