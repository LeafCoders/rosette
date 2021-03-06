package se.leafcoders.rosette.endpoint.group;

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

public class GroupControllerUserTest extends AbstractControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Group.class);
    private final Matcher<?> USER_PARAMS = allOf(hasKey("id"), hasKey("email"), hasKey("firstName"),
            hasKey("lastName"));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void addUserToGroup() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);
        final Group group = groupRepository.save(GroupData.admins());

        crt.allAddChildTests(user1, "users:read,groups:read,groups:update",
                "/api/groups/" + group.getId() + "/users",
                user1.getId(), "")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIdOf(user1)))
                .andExpect(jsonPath("$[0]", USER_PARAMS));

        // Add another user
        crt.postSuccessWithOk(user1, "/api/groups/" + group.getId() + "/users/" + user2.getId(), "")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(user1)))
                .andExpect(jsonPath("$[1].id", isIdOf(user2)))
                .andExpect(jsonPath("$[0]", USER_PARAMS))
                .andExpect(jsonPath("$[1]", USER_PARAMS));

        // Add the same user shall fail
        crt.postExpectForbidden(ApiError.CHILD_ALREADY_EXIST, user1,
                "/api/groups/" + group.getId() + "/users/" + user2.getId(), "");
    }

    @Test
    public void getUsersOfGroup() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);
        Group group = GroupData.admins();
        group.addUser(user1);
        group.addUser(user2);
        group = groupRepository.save(group);

        crt.allGetChildrenTests(user1, "users:read,groups:read", "/api/groups/" + group.getId() + "/users")
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

        crt.allRemoveChildTests(user1, "users:read,groups:read,groups:update",
                "/api/groups/" + group.getId() + "/users", user2.getId())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", USER_PARAMS))
                .andExpect(jsonPath("$[0].id", isIdOf(user1)));
    }
}
