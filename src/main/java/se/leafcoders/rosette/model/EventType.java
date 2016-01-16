package se.leafcoders.rosette.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.validator.CheckReferenceArray;

@Document(collection = "eventTypes")
public class EventType extends TypeBasedModel {

	@NotNull(message = "eventType.resourceTypes.notNull")
	@CheckReferenceArray(model = ResourceType.class)
    private List<ResourceType> resourceTypes;

    private DefaultSetting<Boolean> hasPublicEvents;

    @Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
    	EventType eventTypeUpdate = (EventType) updateFrom;
    	if (rawData.has("resourceTypes")) {
    		setResourceTypes(eventTypeUpdate.getResourceTypes());
    	}
    	if (rawData.has("hasPublicEvents")) {
    		setHasPublicEvents(eventTypeUpdate.getHasPublicEvents());
    	}
    	super.update(rawData, updateFrom);
    }

    // Getter and setters

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

	public DefaultSetting<Boolean> getHasPublicEvents() {
		return hasPublicEvents;
	}

	public void setHasPublicEvents(DefaultSetting<Boolean> hasPublicEvents) {
		this.hasPublicEvents = hasPublicEvents;
	}
}
