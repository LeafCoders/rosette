package se.leafcoders.rosette.model.education;

import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.model.reference.UserRefOrText;
import se.leafcoders.rosette.validator.CheckReference;
import se.leafcoders.rosette.validator.HasRefOrText;

public class SimpleEducation extends Education {

    @HasRefOrText(message = "simpleEducation.author.oneMustBeSet")
    @CheckReference(model = Location.class, dbKey = "author.ref.id")
    private UserRefOrText author;
    
    // Constructors

    public SimpleEducation() {
        super("simple");
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        SimpleEducation simpleEducationUpdate = (SimpleEducation) updateFrom;
        if (rawData.has("author")) {
            setAuthor(simpleEducationUpdate.getAuthor());
        }
        if (rawData.has("time")) {
            setTime(simpleEducationUpdate.getTime());
        }
        
        setAuthorName(simpleEducationUpdate.getAuthorName());
        super.update(rawData, updateFrom);
    }

    // Getters and setters

    public UserRefOrText getAuthor() {
        return author;
    }

    public void setAuthor(UserRefOrText author) {
        this.author = author;
    }
}
