package se.leafcoders.rosette.endpoint.group;

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

public class GroupControllerTest extends AbstractControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Group.class);
    private final Matcher<?> USER_REF_PARAMS = allOf(hasKey("id"), not(hasKey("email")), hasKey("firstName"),
            hasKey("lastName"));

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getGroup() throws Exception {
        user1 = givenUser(user1);
        Group group = groupRepository.save(GroupData.admins());
        group.addUser(user1);
        group = groupRepository.save(group);

        crt.allGetOneTests(user1, "groups:read", "/api/groups", group.getId())
                .andExpect(jsonPath("$.idAlias", is(group.getIdAlias())))
                .andExpect(jsonPath("$.name", is(group.getName())))
                .andExpect(jsonPath("$.description", is(group.getDescription())))
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0]", USER_REF_PARAMS));
    }

    @Test
    public void getGroups() throws Exception {
        user1 = givenUser(user1);
        final Group group1 = groupRepository.save(GroupData.admins());
        final Group group2 = groupRepository.save(GroupData.users());

        crt.allGetManyTests(user1, "groups:read", "/api/groups")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(group1)))
                .andExpect(jsonPath("$[1].id", isIdOf(group2)));
    }

    @Test
    public void createGroup() throws Exception {
        user1 = givenUser(user1);
        final GroupIn group = GroupData.newGroup();

        crt.allPostTests(user1, "groups:create", "/api/groups", json(group))
                .andExpect(jsonPath("$.idAlias", is(group.getIdAlias())))
                .andExpect(jsonPath("$.name", is(group.getName())))
                .andExpect(jsonPath("$.description", is(group.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/groups", json(GroupData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/groups", json(GroupData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateGroup() throws Exception {
        user1 = givenUser(user1);
        final Group group = groupRepository.save(GroupData.admins());

        String jsonData = mapToJson(data -> data.put("name", "Super admins"));

        crt.allPutTests(user1, "groups:update", "/api/groups", group.getId(), jsonData)
                .andExpect(jsonPath("$.idAlias", is(group.getIdAlias())))
                .andExpect(jsonPath("$.name", is("Super admins")))
                .andExpect(jsonPath("$.description", is(group.getDescription())));
    }

    @Test
    public void deleteGroup() throws Exception {
        user1 = givenUser(user1);
        final Group group = groupRepository.save(GroupData.users());

        crt.allDeleteTests(user1, "groups:delete", "/api/groups", group.getId());
    }
}
