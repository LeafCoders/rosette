package se.leafcoders.rosette.model.education;

import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.model.TypeBasedModel;
import se.leafcoders.rosette.model.resource.UserResourceType;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "educationTypes")
public class EducationType extends TypeBasedModel {
    @HasRef(message = "educationType.eventType.mustBeSet")
    private EventType eventType;

    @HasRef(message = "educationType.authorResourceType.mustBeSet")
    private UserResourceType authorResourceType;    
	
    // Constructors

    public EducationType() {
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        EducationType educationTypeUpdate = (EducationType) updateFrom;
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
