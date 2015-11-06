package se.leafcoders.rosette.model.education;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.TypeBasedModel;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Document(collection = "educationTypes")
//The following annotations uses the property 'type' to decide which class to create
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = EventEducationType.class, name = "event")
})
public abstract class EducationType extends TypeBasedModel {
	@NotEmpty(message = "educationType.type.notEmpty")
    protected String type;

    // Constructors

    public EducationType(String type) {
    	this.type = type;
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
