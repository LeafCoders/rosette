package se.ryttargardskyrkan.rosette.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eventTypes")
public class EventType extends IdBasedModel {

    @NotEmpty(message = "eventType.name.notEmpty")
    private String name;

    // Getter and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
