package se.ryttargardskyrkan.rosette.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.model.resource.ResourceType;

@Document(collection = "eventTypes")
public class EventType extends TypeBasedModel {
    @NotNull(message = "eventType.resourceTypes.notNull")
    private List<ObjectReference<ResourceType>> resourceTypes;

    // Getter and setters

    public List<ObjectReference<ResourceType>> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ObjectReference<ResourceType>> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
}
