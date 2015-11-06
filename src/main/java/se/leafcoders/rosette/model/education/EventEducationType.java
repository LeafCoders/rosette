package se.leafcoders.rosette.model.education;

import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.model.resource.UserResourceType;
import se.leafcoders.rosette.validator.HasRef;

public class EventEducationType extends EducationType {
    @HasRef(message = "eventEducationType.eventType.mustBeSet")
    private EventType eventType;

    @HasRef(message = "eventEducationType.authorResourceType.mustBeSet")
    private UserResourceType authorResourceType;
	
    // Constructors

    public EventEducationType() {
		super("event");
    }

    @Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
    	EventEducationType educationTypeUpdate = (EventEducationType) updateFrom;
    	if (rawData.has("eventType")) {
    		setEventType(educationTypeUpdate.getEventType());
    	}
    	if (rawData.has("authorResourceType")) {
    		setAuthorResourceType(educationTypeUpdate.getAuthorResourceType());
    	}
    	super.update(rawData, updateFrom);
    }

    // Getters and setters

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public UserResourceType getAuthorResourceType() {
        return authorResourceType;
    }

    public void setAuthorResourceType(UserResourceType authorResourceType) {
        this.authorResourceType = authorResourceType;
    }

}
