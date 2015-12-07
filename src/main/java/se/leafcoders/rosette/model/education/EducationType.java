package se.leafcoders.rosette.model.education;

import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.model.TypeBasedModel;
import se.leafcoders.rosette.model.resource.UserResourceType;
import se.leafcoders.rosette.model.upload.UploadFolder;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "educationTypes")
public class EducationType extends TypeBasedModel {
    @HasRef(message = "educationType.eventType.mustBeSet")
    private EventType eventType;

    @HasRef(message = "educationType.authorResourceType.mustBeSet")
    private UserResourceType authorResourceType;

    @HasRef(message = "educationType.uploadFolder.mustBeSet")
    private UploadFolder uploadFolder;

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
        if (rawData.has("uploadFolder")) {
            setUploadFolder(educationTypeUpdate.getUploadFolder());
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

    public UploadFolder getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(UploadFolder uploadFolder) {
        this.uploadFolder = uploadFolder;
    }
}
