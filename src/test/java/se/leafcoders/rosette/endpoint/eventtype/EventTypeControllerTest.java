package se.leafcoders.rosette.endpoint.eventtype;

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

public class EventTypeControllerTest extends AbstractControllerTest {

    @Autowired
    private EventTypeRepository eventTypeRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, EventType.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getEventType() throws Exception {
        user1 = givenUser(user1);
        EventType prayerMeeting = eventTypeRepository.save(EventTypeData.prayerMeeting());

        crt.allGetOneTests(user1, "eventTypes:read", "/api/eventTypes", prayerMeeting.getId())
                .andExpect(jsonPath("$.idAlias", is(prayerMeeting.getIdAlias())))
                .andExpect(jsonPath("$.name", is(prayerMeeting.getName())))
                .andExpect(jsonPath("$.description", is(prayerMeeting.getDescription())));
    }

    @Test
    public void getEventTypes() throws Exception {
        user1 = givenUser(user1);
        EventType prayerMeeting = eventTypeRepository.save(EventTypeData.prayerMeeting());
        EventType sermon = eventTypeRepository.save(EventTypeData.sermon());

        crt.allGetManyTests(user1, "eventTypes:read", "/api/eventTypes")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(prayerMeeting)))
                .andExpect(jsonPath("$[1].id", isIdOf(sermon)));
    }

    @Test
    public void createEventType() throws Exception {
        user1 = givenUser(user1);
        EventTypeIn eventType = EventTypeData.bon();

        crt.allPostTests(user1, "eventTypes:create", "/api/eventTypes", json(eventType))
                .andExpect(jsonPath("$.idAlias", is(eventType.getIdAlias())))
                .andExpect(jsonPath("$.name", is(eventType.getName())))
                .andExpect(jsonPath("$.description", is(eventType.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/eventTypes", json(EventTypeData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/eventTypes", json(EventTypeData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateEventType() throws Exception {
        user1 = givenUser(user1);
        EventType admins = eventTypeRepository.save(EventTypeData.prayerMeeting());

        String jsonData = mapToJson(data -> data.put("name", "Super admins"));

        crt.allPutTests(user1, "eventTypes:update", "/api/eventTypes", admins.getId(), jsonData)
                .andExpect(jsonPath("$.idAlias", is(admins.getIdAlias())))
                .andExpect(jsonPath("$.name", is("Super admins")))
                .andExpect(jsonPath("$.description", is(admins.getDescription())));
    }

    @Test
    public void deleteEventType() throws Exception {
        user1 = givenUser(user1);
        EventType sermon = eventTypeRepository.save(EventTypeData.sermon());

        crt.allDeleteTests(user1, "eventTypes:delete", "/api/eventTypes", sermon.getId());
    }
}
