package se.leafcoders.rosette.endpoint.resource;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.test.matcher.Matchers.isIdOf;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.CommonRequestTests;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeData;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRepository;

public class ResourceControllerResourceTypeTest extends AbstractControllerTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Resource.class);
    private final Matcher<?> RESOURCE_TYPE_PARAMS = allOf(hasKey("id"), hasKey("idAlias"), hasKey("name"),
            hasKey("description"));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void addResourceTypeToResource() throws Exception {
        user1 = givenUser(user1);
        final Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());
        final ResourceType resourceType1 = resourceTypeRepository.save(ResourceTypeData.sound());
        final ResourceType resourceType2 = resourceTypeRepository.save(ResourceTypeData.preacher());

        crt.allAddChildTests(user1, "resourceTypes:read,resources:read,resources:update",
                "/api/resources/" + resource.getId() + "/resourceTypes", resourceType1.getId(), "")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIdOf(resourceType1)))
                .andExpect(jsonPath("$[0]", RESOURCE_TYPE_PARAMS));

        // Add another resource type
        crt.postSuccessWithOk(user1,
                "/api/resources/" + resource.getId() + "/resourceTypes/" + resourceType2.getId(),
                "")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(resourceType1)))
                .andExpect(jsonPath("$[1].id", isIdOf(resourceType2)))
                .andExpect(jsonPath("$[0]", RESOURCE_TYPE_PARAMS))
                .andExpect(jsonPath("$[1]", RESOURCE_TYPE_PARAMS));

        // Add the same resource type shall fail
        crt.postExpectForbidden(ApiError.CHILD_ALREADY_EXIST, user1,
                "/api/resources/" + resource.getId() + "/resourceTypes/" + resourceType2.getId(), "");
    }

    @Test
    public void getResourceTypesOfResource() throws Exception {
        user1 = givenUser(user1);
        final ResourceType resourceType1 = resourceTypeRepository.save(ResourceTypeData.sound());
        final ResourceType resourceType2 = resourceTypeRepository.save(ResourceTypeData.preacher());
        Resource resource = ResourceData.lasseLjudtekniker();
        resource.addResourceType(resourceType1);
        resource.addResourceType(resourceType2);
        resource = resourceRepository.save(resource);

        crt.allGetChildrenTests(user1, "resourceTypes:read,resources:read",
                "/api/resources/" + resource.getId() + "/resourceTypes")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", RESOURCE_TYPE_PARAMS))
                .andExpect(jsonPath("$[1]", RESOURCE_TYPE_PARAMS));
    }

    @Test
    public void removeResourceTypeFromResource() throws Exception {
        user1 = givenUser(user1);
        final ResourceType resourceType1 = resourceTypeRepository.save(ResourceTypeData.sound());
        final ResourceType resourceType2 = resourceTypeRepository.save(ResourceTypeData.preacher());
        Resource resource = ResourceData.lasseLjudtekniker();
        resource.addResourceType(resourceType1);
        resource.addResourceType(resourceType2);
        resource = resourceRepository.save(resource);

        crt.allRemoveChildTests(user1, "resourceTypes:read,resources:read,resources:update",
                "/api/resources/" + resource.getId() + "/resourceTypes", resourceType2.getId())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", RESOURCE_TYPE_PARAMS))
                .andExpect(jsonPath("$[0].id", isIdOf(resourceType1)));
    }
}
