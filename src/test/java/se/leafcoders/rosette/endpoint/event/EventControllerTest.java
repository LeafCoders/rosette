package se.leafcoders.rosette.endpoint.event;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.test.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.test.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.CommonRequestTests;
import se.leafcoders.rosette.endpoint.eventtype.EventType;
import se.leafcoders.rosette.endpoint.eventtype.EventTypeData;
import se.leafcoders.rosette.endpoint.eventtype.EventTypeRepository;

public class EventControllerTest extends AbstractControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Event.class);
    private EventType eventType = null;

    @Before
    public void setup() throws Exception {
        super.setup();
        eventType = eventTypeRepository.save(EventTypeData.prayerMeeting());
    }

    @Test
    public void getEvent() throws Exception {
        user1 = givenUser(user1);
        Event eventSummer = eventRepository.save(EventData.existingEvent(eventType, "2020-06-01T10:00:00", 90));
        Event eventWinter = eventRepository.save(EventData.existingEvent(eventType, "2020-11-05T10:00:00", 90));

        // Summer time UTC+2
        crt.allGetOneTests(user1, "events:read", "/api/events", eventSummer.getId())
                .andExpect(jsonPath("$.startTime", is("2020-06-01T12:00:00")))
                .andExpect(jsonPath("$.endTime", is("2020-06-01T13:30:00")))
                .andExpect(jsonPath("$.title", is(eventSummer.getTitle())))
                .andExpect(jsonPath("$.description", is(eventSummer.getDescription())))
                .andExpect(jsonPath("$.eventType.id", isIdOf(eventSummer.getEventType())));

        // Winter time UTC+1
        crt.getOneSuccess(user1, "/api/events", eventWinter.getId())
                .andExpect(jsonPath("$.startTime", is("2020-11-05T11:00:00")))
                .andExpect(jsonPath("$.endTime", is("2020-11-05T12:30:00")))
                .andExpect(jsonPath("$.eventType.id", isIdOf(eventWinter.getEventType())));
    }

    @Test
    public void getEvents() throws Exception {
        user1 = givenUser(user1);
        Event event1 = eventRepository.save(EventData.existingEvent(eventType, "2020-06-27T10:00:00", 90));
        Event event2 = eventRepository.save(EventData.existingEvent(eventType, "2020-06-01T10:00:00", 90));
        Event event3 = eventRepository.save(EventData.existingEvent(eventType, "2020-06-27T10:00:00", 90));

        // Get sorted by start time and id
        crt.allGetManyTests(user1, "events:read", "/api/events?from=2010-01-01T00:00:00")
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", isIdOf(event2)))
                .andExpect(jsonPath("$[1].id", isIdOf(event1)))
                .andExpect(jsonPath("$[2].id", isIdOf(event3)));

        // Get between `from` and `before`
        crt.getManySuccess(user1, "/api/events?from=2010-01-01T00:00:00&before=2020-06-01T12:00:01")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIdOf(event2)));
    }

    @Test
    public void createEvent() throws Exception {
        user1 = givenUser(user1);

        String jsonData = mapToJson(data -> {
            data.put("eventTypeId", eventType.getId());
            data.put("startTime", "2020-10-10T11:00:00");
            data.put("endTime", "2020-10-10T12:00:00");
            data.put("title", "Event title");
            data.put("description", "An event");
            data.put("isPublic", true);
        });

        crt.allPostTests(user1, "eventTypes:read,events:create", "/api/events", jsonData)
                .andExpect(jsonPath("$.eventType.id", is(eventType.getId().intValue())))
                .andExpect(jsonPath("$.startTime", is("2020-10-10T11:00:00")))
                .andExpect(jsonPath("$.endTime", is("2020-10-10T12:00:00")))
                .andExpect(jsonPath("$.title", is("Event title")))
                .andExpect(jsonPath("$.description", is("An event")));

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
        Event event = eventRepository.save(EventData.existingEvent(eventType, "2020-06-01T07:00:00", 60));

        String jsonData = mapToJson(data -> data.put("title", "New title"));

        crt.allPutTests(user1, "events:update", "/api/events", event.getId(), jsonData)
                .andExpect(jsonPath("$.title", is("New title")));
    }

    @Test
    public void deleteEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType, "2020-06-01T07:00:00", 60));

        crt.allDeleteTests(user1, "events:delete", "/api/events", event.getId());
    }
}
