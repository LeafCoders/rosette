package se.leafcoders.rosette.model.reference;

import java.util.Date;

import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.event.Event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EventRef extends IdBasedModel {
    private String title;

    @JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
    @JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
    private Date startTime;

    public EventRef() {}

    public EventRef(Event event) {
        setId(event.getId());
        setTitle(event.getTitle());
        setStartTime(event.getStartTime());
    }

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        Event eventUpdate = (Event) updateFrom;
        if (rawData.has("title")) {
            setTitle(eventUpdate.getTitle());
        }
        if (rawData.has("startTime")) {
            setStartTime(eventUpdate.getStartTime());
        }
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

}
