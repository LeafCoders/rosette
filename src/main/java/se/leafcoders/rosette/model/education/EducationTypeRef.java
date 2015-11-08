package se.leafcoders.rosette.model.education;

import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;

public class EducationTypeRef extends IdBasedModel {
    private String name;

    public EducationTypeRef() {}

    public EducationTypeRef(EducationType educationType) {
        setId(educationType.getId());
        setName(educationType.getName());
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        EducationType educationTypeUpdate = (EducationType) updateFrom;
        if (rawData.has("name")) {
            setName(educationTypeUpdate.getName());
        }
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
