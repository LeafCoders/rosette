package se.leafcoders.rosette.model.education;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.validator.CheckReference;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "educations")
// The following annotations uses the property 'type' to decide which class to create
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventEducation.class, name = "event"),
    @JsonSubTypes.Type(value = SimpleEducation.class, name = "simple")
})
public abstract class Education extends IdBasedModel {
    @NotEmpty(message = "education.type.notEmpty")
    private String type;

    @HasRef(message = "education.educationType.mustBeSet")
    @CheckReference(model = EducationType.class)
    private EducationTypeRef educationType;

    @HasRef(message = "education.educationTheme.mustBeSet")
    @CheckReference(model = EducationTheme.class)
    private EducationThemeRef educationTheme;

    @NotNull(message = "education.time.notEmpty")
    @JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
    @JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
    private Date time;

    private String authorName;
    
    @NotEmpty(message = "education.title.notEmpty")
    private String title;

    @Length(max = 10000, message = "education.content.max10000Chars")
    private String content;

    @Length(max = 10000, message = "education.questions.max10000Chars")
    private String questions;

    @CheckReference
    private UploadResponse recording;

    @NotNull
    @JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
    @JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
    private Date updatedTime;

    // Constructors

    public Education(String type) {
        this.type = type;
        this.setTime(new Date());
        this.updatedTime = new Date();
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        Education educationUpdate = (Education) updateFrom;

        if (educationUpdate.getEducationType() != null && !educationUpdate.getEducationType().getId().equals(getEducationType().getId())) {
            throw new SimpleValidationException(new ValidationError("education", "education.educationType.notAllowedToChange"));
        }

        if (rawData.has("educationTheme")) {
            setEducationTheme(educationUpdate.getEducationTheme());
        }
        if (rawData.has("title")) {
            setTitle(educationUpdate.getTitle());
        }
        if (rawData.has("content")) {
            setContent(educationUpdate.getContent());
        }
        if (rawData.has("questions")) {
            setQuestions(educationUpdate.getQuestions());
        }
        if (rawData.has("recording")) {
            setRecording(educationUpdate.getRecording());
        }
        
        setUpdatedTime(new Date());
    }
    
    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EducationTypeRef getEducationType() {
        return educationType;
    }

    public void setEducationType(EducationTypeRef educationType) {
        this.educationType = educationType;
    }

    public EducationThemeRef getEducationTheme() {
        return educationTheme;
    }

    public void setEducationTheme(EducationThemeRef educationTheme) {
        this.educationTheme = educationTheme;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public UploadResponse getRecording() {
        return recording;
    }

    public void setRecording(UploadResponse recording) {
        this.recording = recording;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
