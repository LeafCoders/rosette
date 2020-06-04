package se.leafcoders.rosette.endpoint.resource;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.test.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.test.matcher.Matchers.isValidationError;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.CommonRequestTests;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeData;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRepository;

public class ResourceControllerTest extends AbstractControllerTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Resource.class);
    private final Matcher<?> RESOURCE_TYPE_REF_PARAMS = allOf(hasKey("id"), not(hasKey("idAlias")), hasKey("name"),
            not(hasKey("description")));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getResource() throws Exception {
        user1 = givenUser(user1);
        Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());
        final ResourceType resourceType = resourceTypeRepository.save(ResourceTypeData.sound());
        resource.addResourceType(resourceType);
        resource = resourceRepository.save(resource);

        crt.allGetOneTests(user1, "resources:read", "/api/resources", resource.getId())
                .andExpect(jsonPath("$.name", is(resource.getName())))
                .andExpect(jsonPath("$.description", is(resource.getDescription())))
                .andExpect(jsonPath("$.resourceTypes", hasSize(1)))
                .andExpect(jsonPath("$.resourceTypes[0]", RESOURCE_TYPE_REF_PARAMS));
    }

    @Test
    public void getResources() throws Exception {
        user1 = givenUser(user1);
        final Resource resource1 = resourceRepository.save(ResourceData.lasseLjudtekniker());
        final Resource resource2 = resourceRepository.save(ResourceData.loffeLjudtekniker());

        crt.allGetManyTests(user1, "resources:read", "/api/resources")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(resource1)))
                .andExpect(jsonPath("$[1].id", isIdOf(resource2)));
    }

    @Test
    public void createResource() throws Exception {
        user1 = givenUser(user1);
        final ResourceIn resource = ResourceData.newResource("Resource", user1.getId());

        crt.allPostTests(user1, "resources:create", "/api/resources", json(resource))
                .andExpect(jsonPath("$.name", is(resource.getName())))
                .andExpect(jsonPath("$.description", is(resource.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/resources", json(ResourceData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/resources", json(ResourceData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateResource() throws Exception {
        user1 = givenUser(user1);
        final Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());

        String jsonData = mapToJson(data -> data.put("name", "Super admins"));

        crt.allPutTests(user1, "resources:update", "/api/resources", resource.getId(), jsonData)
                .andExpect(jsonPath("$.name", is("Super admins")))
                .andExpect(jsonPath("$.description", is(resource.getDescription())));
    }

    @Test
    public void deleteResource() throws Exception {
        user1 = givenUser(user1);
        final Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());

        crt.allDeleteTests(user1, "resources:delete", "/api/resources", resource.getId());
    }
}
