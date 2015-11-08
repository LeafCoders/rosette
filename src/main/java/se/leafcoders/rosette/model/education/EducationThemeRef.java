package se.leafcoders.rosette.model.education;

import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;

public class EducationThemeRef extends IdBasedModel {
    private String title;

    public EducationThemeRef() {}

    public EducationThemeRef(EducationTheme educationTheme) {
        setId(educationTheme.getId());
        setTitle(educationTheme.getTitle());
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        EducationTheme educationThemeUpdate = (EducationTheme) updateFrom;
        if (rawData.has("title")) {
            setTitle(educationThemeUpdate.getTitle());
        }
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
