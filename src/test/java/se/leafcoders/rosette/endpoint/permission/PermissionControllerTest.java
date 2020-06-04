package se.leafcoders.rosette.endpoint.permission;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
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
import se.leafcoders.rosette.endpoint.permissionset.PermissionSet;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetData;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetRepository;

public class PermissionControllerTest extends AbstractControllerTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionSetRepository permissionSetRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Permission.class);
    private final Matcher<?> PERMISSION_SET_PARAMS = allOf(hasKey("id"), hasKey("name"), hasKey("patterns"));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getPermission() throws Exception {
        user1 = givenUser(user1);
        Permission permission = permissionRepository.save(PermissionData.manageEvents());
        final PermissionSet permissionSet = permissionSetRepository.save(PermissionSetData.readEvents());
        permission.addPermissionSet(permissionSet);
        permission = permissionRepository.save(permission);

        crt.allGetOneTests(user1, "permissions:read", "/api/permissions", permission.getId())
                .andExpect(jsonPath("$.name", is(permission.getName())))
                .andExpect(jsonPath("$.patterns", is(permission.getPatterns())))
                .andExpect(jsonPath("$.permissionSets", hasSize(1)))
                .andExpect(jsonPath("$.permissionSets[0]", PERMISSION_SET_PARAMS));
    }

    @Test
    public void getPermissions() throws Exception {
        user1 = givenUser(user1);
        final Permission permission1 = permissionRepository.save(PermissionData.manageEvents());
        final Permission permission2 = permissionRepository.save(PermissionData.manageUsers(user1.getId()));

        crt.allGetManyTests(user1, "permissions:read", "/api/permissions")
                .andExpect(jsonPath("$", hasSize(3))) // One for the permission of user1
                .andExpect(jsonPath("$[0].id", isIdOf(permission1)))
                .andExpect(jsonPath("$[1].id", isIdOf(permission2)));
    }

    @Test
    public void createPermission() throws Exception {
        user1 = givenUser(user1);
        final PermissionIn permission = PermissionData.manageGroups();

        crt.allPostTests(user1, "permissions:create", "/api/permissions", json(permission))
                .andExpect(jsonPath("$.name", is(permission.getName())))
                .andExpect(jsonPath("$.patterns", is(permission.getPatterns())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/permissions", json(PermissionData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("level", ApiString.NOT_NULL)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/permissions", json(PermissionData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("level", ApiString.NUMBER_OUT_OF_RANGE)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updatePermission() throws Exception {
        user1 = givenUser(user1);
        final Permission permission = permissionRepository.save(PermissionData.manageEvents());

        String jsonData = mapToJson(data -> {
            data.put("name", "Edit groups and users");
            data.put("patterns", "users:*,groups:*");
        });

        crt.allPutTests(user1, "permissions:update", "/api/permissions", permission.getId(), jsonData)
                .andExpect(jsonPath("$.name", is("Edit groups and users")))
                .andExpect(jsonPath("$.patterns", is("users:*,groups:*")));
    }

    @Test
    public void deletePermission() throws Exception {
        user1 = givenUser(user1);
        final Permission permission = permissionRepository.save(PermissionData.manageEvents());

        crt.allDeleteTests(user1, "permissions:delete", "/api/permissions", permission.getId());
    }
}
