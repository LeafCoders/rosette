package se.leafcoders.rosette.endpoint.resourcetype;

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

public class ResourceTypeControllerTest extends AbstractControllerTest {

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, ResourceType.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getResourceType() throws Exception {
        user1 = givenUser(user1);
        final ResourceType sound = resourceTypeRepository.save(ResourceTypeData.sound());

        crt.allGetOneTests(user1, "resourceTypes:read", "/api/resourceTypes", sound.getId())
                .andExpect(jsonPath("$.idAlias", is(sound.getIdAlias())))
                .andExpect(jsonPath("$.name", is(sound.getName())))
                .andExpect(jsonPath("$.description", is(sound.getDescription())));
    }

    @Test
    public void getResourceTypes() throws Exception {
        user1 = givenUser(user1);
        final ResourceType sound = resourceTypeRepository.save(ResourceTypeData.sound());
        final ResourceType preatcher = resourceTypeRepository.save(ResourceTypeData.preacher());

        crt.allGetManyTests(user1, "resourceTypes:read", "/api/resourceTypes")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(sound)))
                .andExpect(jsonPath("$[1].id", isIdOf(preatcher)));
    }

    @Test
    public void createResourceType() throws Exception {
        user1 = givenUser(user1);
        final ResourceTypeIn resourceType = ResourceTypeData.newResourceType();

        crt.allPostTests(user1, "resourceTypes:create", "/api/resourceTypes", json(resourceType))
                .andExpect(jsonPath("$.idAlias", is(resourceType.getIdAlias())))
                .andExpect(jsonPath("$.name", is(resourceType.getName())))
                .andExpect(jsonPath("$.description", is(resourceType.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/resourceTypes", json(ResourceTypeData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/resourceTypes", json(ResourceTypeData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateResourceType() throws Exception {
        user1 = givenUser(user1);
        final ResourceType sound = resourceTypeRepository.save(ResourceTypeData.sound());

        String jsonData = mapToJson(data -> data.put("name", "Super sound"));

        crt.allPutTests(user1, "resourceTypes:update", "/api/resourceTypes", sound.getId(), jsonData)
                .andExpect(jsonPath("$.idAlias", is(sound.getIdAlias())))
                .andExpect(jsonPath("$.name", is("Super sound")))
                .andExpect(jsonPath("$.description", is(sound.getDescription())));
    }

    @Test
    public void deleteResourceType() throws Exception {
        user1 = givenUser(user1);
        final ResourceType preatcher = resourceTypeRepository.save(ResourceTypeData.preacher());

        crt.allDeleteTests(user1, "resourceTypes:delete", "/api/resourceTypes", preatcher.getId());
    }
}
