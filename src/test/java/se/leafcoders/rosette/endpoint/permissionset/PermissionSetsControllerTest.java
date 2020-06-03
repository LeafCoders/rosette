package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.data.PermissionSetData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.PermissionSet;
import se.leafcoders.rosette.persistence.repository.PermissionSetRepository;

public class PermissionSetsControllerTest extends AbstractControllerTest {

    @Autowired
    private PermissionSetRepository permissionSetRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, PermissionSet.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getPermissionSet() throws Exception {
        user1 = givenUser(user1);
        final PermissionSet readEvents = permissionSetRepository.save(PermissionSetData.readEvents());

        crt.allGetOneTests(user1, "permissionSets:read", "/api/permissionSets", readEvents.getId())
                .andExpect(jsonPath("$.name", is(readEvents.getName())))
                .andExpect(jsonPath("$.patterns", is(readEvents.getPatterns())));
    }

    @Test
    public void getPermissionSets() throws Exception {
        user1 = givenUser(user1);
        final PermissionSet viewAll = permissionSetRepository.save(PermissionSetData.viewAll());
        final PermissionSet readEvents = permissionSetRepository.save(PermissionSetData.readEvents());

        crt.allGetManyTests(user1, "permissionSets:read", "/api/permissionSets")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(readEvents)))
                .andExpect(jsonPath("$[1].id", isIdOf(viewAll)));
    }

    @Test
    public void createPermissionSet() throws Exception {
        user1 = givenUser(user1);

        String jsonData = mapToJson(data -> {
            data.put("name", "Delete all");
            data.put("patterns", "*:delete");
        });

        crt.allPostTests(user1, "permissionSets:create", "/api/permissionSets", jsonData)
                .andExpect(jsonPath("$.name", is("Delete all")))
                .andExpect(jsonPath("$.patterns", is("*:delete")));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/permissionSets", json(PermissionSetData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("name", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[1]", isValidationError("patterns", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/permissionSets", json(PermissionSetData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("name", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[1]", isValidationError("patterns", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updatePermissionSet() throws Exception {
        user1 = givenUser(user1);
        final PermissionSet viewAll = permissionSetRepository.save(PermissionSetData.viewAll());

        String jsonData = mapToJson(data -> {
            data.put("name", "View events");
            data.put("patterns", "events:view");
        });

        crt.allPutTests(user1, "permissionSets:update", "/api/permissionSets", viewAll.getId(), jsonData)
                .andExpect(jsonPath("$.name", is("View events")))
                .andExpect(jsonPath("$.patterns", is("events:view")));
    }

    @Test
    public void deletePermissionSet() throws Exception {
        user1 = givenUser(user1);
        final PermissionSet viewAll = permissionSetRepository.save(PermissionSetData.viewAll());

        crt.allDeleteTests(user1, "permissionSets:delete", "/api/permissionSets", viewAll.getId());
    }
}
