package se.leafcoders.rosette.model.education;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;

public class SimpleEducation extends Education {

    @NotEmpty(message = "education.authorName.notEmpty")
    private String authorName;

    // Constructors

    public SimpleEducation() {
        super("simple");
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        SimpleEducation simpleEducationUpdate = (SimpleEducation) updateFrom;
        if (rawData.has("authorName")) {
            setAuthorName(simpleEducationUpdate.getAuthorName());
        }
        if (rawData.has("time")) {
            setTime(simpleEducationUpdate.getTime());
        }
        super.update(rawData, updateFrom);
    }

    // Getters and setters

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
