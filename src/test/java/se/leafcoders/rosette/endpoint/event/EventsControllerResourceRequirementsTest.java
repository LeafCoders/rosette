package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.data.EventData;
import se.leafcoders.rosette.data.EventTypeData;
import se.leafcoders.rosette.data.ResourceRequirementData;
import se.leafcoders.rosette.data.ResourceTypeData;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.EventType;
import se.leafcoders.rosette.persistence.model.ResourceRequirement;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.EventRepository;
import se.leafcoders.rosette.persistence.repository.EventTypeRepository;
import se.leafcoders.rosette.persistence.repository.ResourceRequirementRepository;
import se.leafcoders.rosette.persistence.repository.ResourceTypeRepository;

public class EventsControllerResourceRequirementsTest extends AbstractControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    @Autowired
    private ResourceRequirementRepository resourceRequirementRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Event.class);
    private final Matcher<?> RESOURCE_REQUIREMENT_PARAMS = allOf(hasKey("id"), hasKey("resourceType"),
            hasKey("resources"));

    private EventType eventType = null;
    private ResourceType resourceType1 = null;
    private ResourceType resourceType2 = null;

    @Before
    public void setup() throws Exception {
        super.setup();

        eventType = eventTypeRepository.save(EventTypeData.prayerMeeting());
        resourceType1 = resourceTypeRepository.save(ResourceTypeData.sound());
        resourceType2 = resourceTypeRepository.save(ResourceTypeData.preacher());
    }

    @Test
    public void addResourceRequirementToEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType));

        String jsonData1 = mapToJson(data -> data.put("resourceTypeId", resourceType1.getId()));
        String jsonData2 = mapToJson(data -> data.put("resourceTypeId", resourceType2.getId()));

        crt.allAddChildTests(user1, "resourceTypes:read,events:read,events:update",
                "/api/events/" + event.getId() + "/resourceRequirements", null, jsonData1)
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", RESOURCE_REQUIREMENT_PARAMS));

        // Add another resource type
        crt.postSuccessWithOk(user1, "/api/events/" + event.getId() + "/resourceRequirements", jsonData2)
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", RESOURCE_REQUIREMENT_PARAMS))
                .andExpect(jsonPath("$[1]", RESOURCE_REQUIREMENT_PARAMS));

        // Add the same resource type shall fail
        crt.postExpectForbidden(ApiError.CHILD_ALREADY_EXIST, user1,
                "/api/events/" + event.getId() + "/resourceRequirements", jsonData2);
    }

    @Test
    public void getResourceRequirementsOfEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType));
        ResourceRequirement resourceRequirement1 = resourceRequirementRepository
                .save(ResourceRequirementData.create(event, resourceType1));
        ResourceRequirement resourceRequirement2 = resourceRequirementRepository
                .save(ResourceRequirementData.create(event, resourceType2));
        event.addResourceRequirement(resourceRequirement1);
        event.addResourceRequirement(resourceRequirement2);
        event = eventRepository.save(event);

        crt.allGetChildrenTests(user1, "resourceTypes:read,events:read",
                "/api/events/" + event.getId() + "/resourceRequirements")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", RESOURCE_REQUIREMENT_PARAMS))
                .andExpect(jsonPath("$[1]", RESOURCE_REQUIREMENT_PARAMS));
    }

    @Test
    public void removeResourceRequirementFromEvent() throws Exception {
        user1 = givenUser(user1);
        Event event = eventRepository.save(EventData.existingEvent(eventType));
        ResourceRequirement resourceRequirement1 = resourceRequirementRepository
                .save(ResourceRequirementData.create(event, resourceType1));
        ResourceRequirement resourceRequirement2 = resourceRequirementRepository
                .save(ResourceRequirementData.create(event, resourceType2));
        event.addResourceRequirement(resourceRequirement1);
        event.addResourceRequirement(resourceRequirement2);
        event = eventRepository.save(event);

        crt.allRemoveChildTests(user1, "resourceTypes:read,events:read,events:update",
                "/api/events/" + event.getId() + "/resourceRequirements", resourceRequirement2.getId())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", RESOURCE_REQUIREMENT_PARAMS))
                .andExpect(jsonPath("$[0].id", isIdOf(resourceRequirement1)));
    }
}
