package se.leafcoders.rosette.endpoint.permission;

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
import se.leafcoders.rosette.endpoint.permissionset.PermissionSet;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetData;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetRepository;

public class PermissionControllerPermissionSetTest extends AbstractControllerTest {

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
    public void addPermissionSetToPermission() throws Exception {
        user1 = givenUser(user1);
        final Permission permission = permissionRepository.save(PermissionData.manageEvents());
        final PermissionSet permissionSet1 = permissionSetRepository.save(PermissionSetData.viewAll());
        final PermissionSet permissionSet2 = permissionSetRepository.save(PermissionSetData.readEvents());

        crt.allAddChildTests(user1, "permissionSets:read,permissions:read,permissions:update",
                "/api/permissions/" + permission.getId() + "/permissionSets", permissionSet1.getId(),
                "")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIdOf(permissionSet1)))
                .andExpect(jsonPath("$[0]", PERMISSION_SET_PARAMS));

        // Add another permission type
        crt.postSuccessWithOk(user1,
                "/api/permissions/" + permission.getId() + "/permissionSets/" + permissionSet2.getId(),
                "")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(permissionSet1)))
                .andExpect(jsonPath("$[1].id", isIdOf(permissionSet2)))
                .andExpect(jsonPath("$[0]", PERMISSION_SET_PARAMS))
                .andExpect(jsonPath("$[1]", PERMISSION_SET_PARAMS));

        // Add the same permission type shall fail
        crt.postExpectForbidden(ApiError.CHILD_ALREADY_EXIST, user1,
                "/api/permissions/" + permission.getId() + "/permissionSets/" + permissionSet2.getId(),
                "");
    }

    @Test
    public void getPermissionSetsOfPermission() throws Exception {
        user1 = givenUser(user1);
        final PermissionSet permissionSet1 = permissionSetRepository.save(PermissionSetData.viewAll());
        final PermissionSet permissionSet2 = permissionSetRepository.save(PermissionSetData.readEvents());
        Permission permission = PermissionData.manageEvents();
        permission.addPermissionSet(permissionSet1);
        permission.addPermissionSet(permissionSet2);
        permission = permissionRepository.save(permission);

        crt.allGetChildrenTests(user1, "permissionSets:read,permissions:read",
                "/api/permissions/" + permission.getId() + "/permissionSets")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", PERMISSION_SET_PARAMS))
                .andExpect(jsonPath("$[1]", PERMISSION_SET_PARAMS));
    }

    @Test
    public void removePermissionSetFromPermission() throws Exception {
        user1 = givenUser(user1);
        final PermissionSet permissionSet1 = permissionSetRepository.save(PermissionSetData.viewAll());
        final PermissionSet permissionSet2 = permissionSetRepository.save(PermissionSetData.readEvents());
        Permission permission = PermissionData.manageEvents();
        permission.addPermissionSet(permissionSet1);
        permission.addPermissionSet(permissionSet2);
        permission = permissionRepository.save(permission);

        crt.allRemoveChildTests(user1, "permissionSets:read,permissions:read,permissions:update",
                "/api/permissions/" + permission.getId() + "/permissionSets", permissionSet2.getId())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", PERMISSION_SET_PARAMS))
                .andExpect(jsonPath("$[0].id", isIdOf(permissionSet1)));
    }
}
