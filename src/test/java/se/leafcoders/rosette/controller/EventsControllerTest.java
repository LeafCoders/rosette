package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.controller.dto.EventIn;
import se.leafcoders.rosette.data.EventData;
import se.leafcoders.rosette.data.EventTypeData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.EventType;
import se.leafcoders.rosette.persistence.repository.EventRepository;
import se.leafcoders.rosette.persistence.repository.EventTypeRepository;

public class EventsControllerTest extends AbstractControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, Event.class);

    private EventType eventType = null;

    @Before
    public void setup() throws Exception {
        super.setup();

        eventType = eventTypeRepository.save(EventTypeData.prayerMeeting());
    }

    @Test
    public void getEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType));

        crt.allGetOneTests(user1, "events:read", "/api/events", event.getId())
            .andExpect(jsonPath("$.startTime", isDateTime(event.getStartTime())))
            .andExpect(jsonPath("$.endTime", isDateTime(event.getEndTime())))
            .andExpect(jsonPath("$.title", is(event.getTitle())))
            .andExpect(jsonPath("$.description", is(event.getDescription())))
            .andExpect(jsonPath("$.eventType.id", isIdOf(event.getEventType())));
    }

    @Test
    public void getEvents() throws Exception {
        user1 = givenUser(user1);
        Event event1 = eventRepository.save(EventData.existingEvent(eventType));
        Event event2 = eventRepository.save(EventData.existingEvent(eventType));
        Event event3 = eventRepository.save(EventData.existingEvent(eventType));

        crt.allGetManyTests(user1, "events:read", "/api/events?from=2010-01-02T12:34:56")
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", isIdOf(event1)))
            .andExpect(jsonPath("$[1].id", isIdOf(event2)))
            .andExpect(jsonPath("$[2].id", isIdOf(event3)));
    }

    @Test
    public void createEvent() throws Exception {
        user1 = givenUser(user1);
        EventIn event = EventData.newEvent(eventType);

        crt.allPostTests(user1, "eventTypes:read,events:create", "/api/events", json(event))
            .andExpect(jsonPath("$.startTime", isDateTime(event.getStartTime())))
            .andExpect(jsonPath("$.endTime", isDateTime(event.getEndTime())))
            .andExpect(jsonPath("$.title", is(event.getTitle())))
            .andExpect(jsonPath("$.description", is(event.getDescription())))
            .andExpect(jsonPath("$.eventType.id", is(event.getEventTypeId().intValue())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/events", json(EventData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0]", isValidationError("eventTypeId", ApiString.NOT_NULL)))
            .andExpect(jsonPath("$[1]", isValidationError("startTime", ApiString.NOT_NULL)))
            .andExpect(jsonPath("$[2]", isValidationError("title", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/events", json(EventData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0]", isValidationError("endTime", ApiString.DATETIME_MUST_BE_AFTER)))
            .andExpect(jsonPath("$[1]", isValidationError("eventTypeId", ApiString.NOT_NULL)))
            .andExpect(jsonPath("$[2]", isValidationError("title", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType));

        String jsonData = mapToJson(data -> data.put("title", "New title"));

        crt.allPutTests(user1, "events:update", "/api/events", event.getId(), jsonData)
            .andExpect(jsonPath("$.title", is("New title")));
    }

    @Test
    public void deleteEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType));

        crt.allDeleteTests(user1, "events:delete", "/api/events", event.getId());
    }
}
