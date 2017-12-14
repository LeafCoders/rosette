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

import se.leafcoders.rosette.controller.dto.GroupIn;
import se.leafcoders.rosette.data.GroupData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.repository.GroupRepository;

public class GroupsControllerTest extends AbstractControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, Group.class);

    private Matcher<?> USER_REF_PARAMS = allOf(hasKey("id"), not(hasKey("email")), hasKey("firstName"), hasKey("lastName"));

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

        crt.allGetOneTests(user1, "groups:read", "/groups", group.getId())
            .andExpect(jsonPath("$.idAlias", is(group.getIdAlias())))
            .andExpect(jsonPath("$.name", is(group.getName())))
            .andExpect(jsonPath("$.description", is(group.getDescription())))
            .andExpect(jsonPath("$.users", hasSize(1)))
            .andExpect(jsonPath("$.users[0]", USER_REF_PARAMS));
    }

    @Test
    public void getGroups() throws Exception {
        user1 = givenUser(user1);
        Group group1 = groupRepository.save(GroupData.admins());
        Group group2 = groupRepository.save(GroupData.users());

        crt.allGetManyTests(user1, "groups:read", "/groups")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(group1)))
            .andExpect(jsonPath("$[1].id", isIdOf(group2)));
    }

    @Test
    public void createGroup() throws Exception {
        user1 = givenUser(user1);
        GroupIn group = GroupData.newGroup();

        crt.allPostTests(user1, "groups:create", "/groups", json(group))
            .andExpect(jsonPath("$.idAlias", is(group.getIdAlias())))
            .andExpect(jsonPath("$.name", is(group.getName())))
            .andExpect(jsonPath("$.description", is(group.getDescription())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/groups", json(GroupData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/groups", json(GroupData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateGroup() throws Exception {
        user1 = givenUser(user1);
        Group group = groupRepository.save(GroupData.admins());

        String jsonData = mapToJson(
            data -> {
                data.put("name", "Super admins");
                return data;
            }
        );

        crt.allPutTests(user1, "groups:update", "/groups", group.getId(), jsonData)
            .andExpect(jsonPath("$.idAlias", is(group.getIdAlias())))
            .andExpect(jsonPath("$.name", is("Super admins")))
            .andExpect(jsonPath("$.description", is(group.getDescription())));
    }

    @Test
    public void deleteGroup() throws Exception {
        user1 = givenUser(user1);
        Group group = groupRepository.save(GroupData.users());

        crt.allDeleteTests(user1, "groups:delete", "/groups", group.getId());
    }
}
