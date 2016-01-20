package se.leafcoders.rosette.model.education;

import javax.validation.Valid;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.reference.EventRef;
import se.leafcoders.rosette.validator.HasRef;
import se.leafcoders.rosette.validator.CheckReference;

public class EventEducation extends Education {
    @HasRef(message = "eventEducation.event.mustBeSet")
    @Valid
    @CheckReference(model = Event.class)
    private EventRef event;

    // Constructors

    public EventEducation() {
        super("event");
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        EventEducation eventEducationUpdate = (EventEducation) updateFrom;
        if (rawData.has("event")) {
            setEvent(eventEducationUpdate.getEvent());
        }

        setTime(eventEducationUpdate.getTime());
        setAuthorName(eventEducationUpdate.getAuthorName());
        super.update(rawData, updateFrom);
    }

    // Getters and setters

    public EventRef getEvent() {
        return event;
    }

    public void setEvent(EventRef event) {
        this.event = event;
    }
}
