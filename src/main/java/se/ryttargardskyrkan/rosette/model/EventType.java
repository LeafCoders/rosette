package se.ryttargardskyrkan.rosette.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.model.resource.ResourceType;

@Document(collection = "eventTypes")
public class EventType extends IdBasedModel {
	@Indexed(unique = true)
	@Pattern(regexp = "[a-z][a-zA-Z]+", message = "common.key.notValidFormat")
	private String key;

    @NotEmpty(message = "eventType.name.notEmpty")
    private String name;

    @NotNull(message = "eventType.resourceTypes.notNull")
    private List<ObjectReference<ResourceType>> resourceTypes;

    // Getter and setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectReference<ResourceType>> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ObjectReference<ResourceType>> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
}
