package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.data.GroupData;
import se.leafcoders.rosette.data.PermissionData;
import se.leafcoders.rosette.data.PermissionSetData;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.model.Permission;
import se.leafcoders.rosette.persistence.model.PermissionSet;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.GroupRepository;
import se.leafcoders.rosette.persistence.repository.PermissionRepository;
import se.leafcoders.rosette.persistence.repository.PermissionSetRepository;

public class UsersControllerPermissionsTest extends AbstractControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionSetRepository permissionSetRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, User.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getPermissionsFromOtherUser() throws Exception {
        givenUser(user1);
        givenUser(user2);

        // Not allowed to get permissions for another user
        crt.getOneExpectForbidden(user1, "/api/users", user2.getId());
    }

    @Test
    public void getUserPermissions() throws Exception {
        givenUser(user1);
        givenUser(user2);
        Permission permission = permissionRepository.save(PermissionData.forUser(user1.getId(), "podcasts:create"));
        final PermissionSet permissionSet = permissionSetRepository
                .save(PermissionSetData.ofPatterns("podcasts:update,podcasts:delete"));
        permission.addPermissionSet(permissionSet);
        permissionRepository.save(permission);

        // Not allowed to get permissions for another user
        crt.getOneExpectForbidden(user1, "/api/users", user2.getId());

        // Get permissions for user1
        crt.getSuccess(user1, "/api/users/" + user1.getId() + "/permissions")
                .andExpect(jsonPath("$[0]", is("podcasts:create")))
                .andExpect(jsonPath("$[1]", is("podcasts:delete")))
                .andExpect(jsonPath("$[2]", is("podcasts:update")))
                .andExpect(jsonPath("$[3]", is("users:read:" + user1.getId())))
                .andExpect(jsonPath("$[4]", is("users:update:" + user1.getId())));
    }

    @Test
    public void getGroupPermissions() throws Exception {
        givenUser(user1);
        Group group = groupRepository.save(GroupData.admins());
        group.addUser(user1);
        groupRepository.save(group);
        Permission permission = permissionRepository.save(PermissionData.forGroup(group.getId(), "podcasts:create"));
        final PermissionSet permissionSet = permissionSetRepository
                .save(PermissionSetData.ofPatterns("podcasts:update,podcasts:delete"));
        permission.addPermissionSet(permissionSet);
        permissionRepository.save(permission);

        // Get permissions for user1
        crt.getSuccess(user1, "/api/users/" + user1.getId() + "/permissions")
                .andExpect(jsonPath("$[0]", is("podcasts:create")))
                .andExpect(jsonPath("$[1]", is("podcasts:delete")))
                .andExpect(jsonPath("$[2]", is("podcasts:update")))
                .andExpect(jsonPath("$[3]", is("users:read:" + user1.getId())))
                .andExpect(jsonPath("$[4]", is("users:update:" + user1.getId())));
    }

    @Test
    public void getAllUsersPermissions() throws Exception {
        givenUser(user1);
        Permission permission = permissionRepository.save(PermissionData.forAllUsers("podcasts:create"));
        final PermissionSet permissionSet = permissionSetRepository
                .save(PermissionSetData.ofPatterns("podcasts:update,podcasts:delete"));
        permission.addPermissionSet(permissionSet);
        permissionRepository.save(permission);

        // Get permissions for user1
        crt.getSuccess(user1, "/api/users/" + user1.getId() + "/permissions")
                .andExpect(jsonPath("$[0]", is("podcasts:create")))
                .andExpect(jsonPath("$[1]", is("podcasts:delete")))
                .andExpect(jsonPath("$[2]", is("podcasts:update")))
                .andExpect(jsonPath("$[3]", is("users:read:" + user1.getId())))
                .andExpect(jsonPath("$[4]", is("users:update:" + user1.getId())));
    }
}
