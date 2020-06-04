package se.leafcoders.rosette.endpoint.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;

@Data
public class EventRefOut {

    private Long id;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime startTime;

    private String title;

    public EventRefOut(Event event) {
        id = event.getId();
        startTime = event.getStartTime();
        title = event.getTitle();
    }
}
