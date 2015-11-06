package se.leafcoders.rosette.model.reference;

import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.education.EducationType;

public class EducationTypeRef extends IdBasedModel {
    private String type;
    private String name;

    public EducationTypeRef() {}

    public EducationTypeRef(EducationType educationType) {
        setId(educationType.getId());
        setType(educationType.getType());
        setName(educationType.getName());
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        EducationType educationTypeUpdate = (EducationType) updateFrom;
        if (rawData.has("type")) {
            setType(educationTypeUpdate.getType());
        }
        if (rawData.has("name")) {
            setName(educationTypeUpdate.getName());
        }
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
