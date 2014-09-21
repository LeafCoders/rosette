package se.ryttargardskyrkan.rosette.model.resource;

import javax.validation.constraints.Pattern;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import se.ryttargardskyrkan.rosette.model.IdBasedModel;

@Document(collection = "resourceTypes")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = UserResourceType.class, name = "user"),
    @JsonSubTypes.Type(value = UploadResourceType.class, name = "upload")
})
public abstract class ResourceType extends IdBasedModel {
	@Pattern(regexp = "[a-z][a-zA-Z]+", message = "common.key.notValidFormat")
	private String key;

	@NotEmpty(message = "resourceType.category.notEmpty")
	private String category;

	@NotEmpty(message = "resourceType.name.notEmpty")
	private String name;

	private String description;

	public Update addToUpdateQuery(Update update) {
		update.set("category", category);
		update.set("name", name);
		update.set("description", description);
		return update;
	}

    // Getters and setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
