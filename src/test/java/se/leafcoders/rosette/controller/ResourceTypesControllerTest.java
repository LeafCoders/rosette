package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.controller.dto.ResourceTypeIn;
import se.leafcoders.rosette.data.ResourceTypeData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.ResourceTypeRepository;

public class ResourceTypesControllerTest extends AbstractControllerTest {

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, ResourceType.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getResourceType() throws Exception {
        user1 = givenUser(user1);
        ResourceType sound = resourceTypeRepository.save(ResourceTypeData.sound());

        crt.allGetOneTests(user1, "resourceTypes:read", "/resourceTypes", sound.getId())
            .andExpect(jsonPath("$.idAlias", is(sound.getIdAlias())))
            .andExpect(jsonPath("$.name", is(sound.getName())))
            .andExpect(jsonPath("$.description", is(sound.getDescription())));
    }

    @Test
    public void getResourceTypes() throws Exception {
        user1 = givenUser(user1);
        ResourceType sound = resourceTypeRepository.save(ResourceTypeData.sound());
        ResourceType preatcher = resourceTypeRepository.save(ResourceTypeData.preacher());

        crt.allGetManyTests(user1, "resourceTypes:read", "/resourceTypes")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(sound)))
            .andExpect(jsonPath("$[1].id", isIdOf(preatcher)));
    }

    @Test
    public void createResourceType() throws Exception {
        user1 = givenUser(user1);
        ResourceTypeIn resourceType = ResourceTypeData.newResourceType();

        crt.allPostTests(user1, "resourceTypes:create", "/resourceTypes", json(resourceType))
            .andExpect(jsonPath("$.idAlias", is(resourceType.getIdAlias())))
            .andExpect(jsonPath("$.name", is(resourceType.getName())))
            .andExpect(jsonPath("$.description", is(resourceType.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/resourceTypes", json(ResourceTypeData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/resourceTypes", json(ResourceTypeData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateResourceType() throws Exception {
        user1 = givenUser(user1);
        ResourceType sound = resourceTypeRepository.save(ResourceTypeData.sound());

        String jsonData = mapToJson(
            data -> {
                data.put("name", "Super sound");
                return data;
            }
        );

        crt.allPutTests(user1, "resourceTypes:update", "/resourceTypes", sound.getId(), jsonData)
            .andExpect(jsonPath("$.idAlias", is(sound.getIdAlias())))
            .andExpect(jsonPath("$.name", is("Super sound")))
            .andExpect(jsonPath("$.description", is(sound.getDescription())));
    }

    @Test
    public void deleteResourceType() throws Exception {
        user1 = givenUser(user1);
        ResourceType preatcher = resourceTypeRepository.save(ResourceTypeData.preacher());

        crt.allDeleteTests(user1, "resourceTypes:delete", "/resourceTypes", preatcher.getId());
    }
}
