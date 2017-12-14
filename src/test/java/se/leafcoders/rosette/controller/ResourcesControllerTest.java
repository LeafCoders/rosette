package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.controller.dto.ResourceIn;
import se.leafcoders.rosette.data.ResourceData;
import se.leafcoders.rosette.data.ResourceTypeData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.ResourceRepository;
import se.leafcoders.rosette.persistence.repository.ResourceTypeRepository;

public class ResourcesControllerTest extends AbstractControllerTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, Resource.class);

    private Matcher<?> RESOURCE_TYPE_REF_PARAMS = allOf(hasKey("id"), not(hasKey("idAlias")), hasKey("name"), not(hasKey("description")));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getResource() throws Exception {
        user1 = givenUser(user1);
        Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());
        ResourceType resourceType = resourceTypeRepository.save(ResourceTypeData.sound());
        resource.addResourceType(resourceType);
        resource = resourceRepository.save(resource);

        crt.allGetOneTests(user1, "resources:read", "/resources", resource.getId())
            .andExpect(jsonPath("$.name", is(resource.getName())))
            .andExpect(jsonPath("$.description", is(resource.getDescription())))
            .andExpect(jsonPath("$.resourceTypes", hasSize(1)))
            .andExpect(jsonPath("$.resourceTypes[0]", RESOURCE_TYPE_REF_PARAMS));
    }

    @Test
    public void getResources() throws Exception {
        user1 = givenUser(user1);
        Resource resource1 = resourceRepository.save(ResourceData.lasseLjudtekniker());
        Resource resource2 = resourceRepository.save(ResourceData.loffeLjudtekniker());

        crt.allGetManyTests(user1, "resources:read", "/resources")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(resource1)))
            .andExpect(jsonPath("$[1].id", isIdOf(resource2)));
    }

    @Test
    public void createResource() throws Exception {
        user1 = givenUser(user1);
        ResourceIn resource = ResourceData.newResource("Resource", user1.getId());

        crt.allPostTests(user1, "resources:create", "/resources", json(resource))
            .andExpect(jsonPath("$.name", is(resource.getName())))
            .andExpect(jsonPath("$.description", is(resource.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/resources", json(ResourceData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/resources", json(ResourceData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateResource() throws Exception {
        user1 = givenUser(user1);
        Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());

        String jsonData = mapToJson(
            data -> {
                data.put("name", "Super admins");
                return data;
            }
        );

        crt.allPutTests(user1, "resources:update", "/resources", resource.getId(), jsonData)
            .andExpect(jsonPath("$.name", is("Super admins")))
            .andExpect(jsonPath("$.description", is(resource.getDescription())));
    }

    @Test
    public void deleteResource() throws Exception {
        user1 = givenUser(user1);
        Resource resource = resourceRepository.save(ResourceData.lasseLjudtekniker());

        crt.allDeleteTests(user1, "resources:delete", "/resources", resource.getId());
    }
}
