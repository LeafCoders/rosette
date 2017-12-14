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

import se.leafcoders.rosette.data.GroupData;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.repository.GroupRepository;

public class GroupsControllerUsersTest extends AbstractControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, Group.class);

    private Matcher<?> USER_PARAMS = allOf(hasKey("id"), hasKey("email"), hasKey("firstName"), hasKey("lastName"));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void addUserToGroup() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);
        Group group = groupRepository.save(GroupData.admins());

        crt.allAddChildTests(user1, "users:read,groups:read,groups:update", "/groups/" + group.getId() + "/users", user1.getId(), "")
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", isIdOf(user1)))
            .andExpect(jsonPath("$[0]", USER_PARAMS));

        // Add another user
        crt.postSuccessWithOk(user1, "/groups/" + group.getId() + "/users/" + user2.getId(), "")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(user1)))
            .andExpect(jsonPath("$[1].id", isIdOf(user2)))
            .andExpect(jsonPath("$[0]", USER_PARAMS))
            .andExpect(jsonPath("$[1]", USER_PARAMS));

        // Add the same user shall fail
        crt.postExpectForbidden(ApiError.CHILD_ALREADY_EXIST, user1, "/groups/" + group.getId() + "/users/" + user2.getId(), "");
    }

    @Test
    public void getUsersOfGroup() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);
        Group group = GroupData.admins();
        group.addUser(user1);
        group.addUser(user2);
        group = groupRepository.save(group);

        crt.allGetChildrenTests(user1, "users:read,groups:read", "/groups/" + group.getId() + "/users")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", USER_PARAMS))
            .andExpect(jsonPath("$[1]", USER_PARAMS));
    }

    @Test
    public void removeUserFromGroup() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);
        Group group = GroupData.admins();
        group.addUser(user1);
        group.addUser(user2);
        group = groupRepository.save(group);

        crt.allRemoveChildTests(user1, "users:read,groups:read,groups:update", "/groups/" + group.getId() + "/users", user2.getId())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0]", USER_PARAMS))
            .andExpect(jsonPath("$[0].id", isIdOf(user1)));
    }
}
