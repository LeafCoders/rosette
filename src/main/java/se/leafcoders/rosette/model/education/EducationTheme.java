package se.leafcoders.rosette.model.education;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "educationThemes")
public class EducationTheme extends IdBasedModel {
    @HasRef(message = "educationTheme.educationType.mustBeSet")
    private EducationTypeRef educationType;

    @NotEmpty(message = "educationTheme.title.notEmpty")
    private String title;

    @Length(max = 10000, message = "educationTheme.content.max10000Chars")
    private String content;
    
    // Constructors

    public EducationTheme() {
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        EducationTheme educationThemeUpdate = (EducationTheme) updateFrom;
        if (rawData.has("title")) {
            setTitle(educationThemeUpdate.getTitle());
        }
        if (rawData.has("content")) {
            setContent(educationThemeUpdate.getContent());
        }
        if (rawData.has("educationType")) {
            setEducationType(educationThemeUpdate.getEducationType());
        }
    }

    // Getters and setters

    public EducationTypeRef getEducationType() {
        return educationType;
    }

    public void setEducationType(EducationTypeRef educationType) {
        this.educationType = educationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
