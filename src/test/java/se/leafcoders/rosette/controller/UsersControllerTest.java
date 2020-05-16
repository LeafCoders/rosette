package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import se.leafcoders.rosette.controller.dto.UserIn;
import se.leafcoders.rosette.data.UserData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.User;

public class UsersControllerTest extends AbstractControllerTest {

    private final CommonRequestTests crt = new CommonRequestTests(this, User.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getUser() throws Exception {
        givenUser(user1);
        givenUser(user2);

        // No permission for user2
        crt.getOneExpectForbidden(user1, "/api/users", user2.getId());

        // With permission for user1 (is automatically added)
        givenPermissionForUser(user1, "users:read:" + user1.getId());
        crt.getOneSuccess(user1, "/api/users", user1.getId())
                .andExpect(jsonPath("$.id", isIdOf(user1)))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user1.getLastName())));

        // Not found
        givenPermissionForUser(user1, "users:read");
        crt.getOneExpectNotFound(user1, "/api/users", 999444L);
    }

    @Test
    public void getUsers() throws Exception {
        givenUser(user1);
        givenUser(user2);

        // No permission (except for automatically added)
        crt.getManySuccess(user1, "/api/users")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIdOf(user1)))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[0].firstName", is(user1.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(user1.getLastName())));

        // With permission
        givenPermissionForUser(user1, "users:read");
        crt.getManySuccess(user1, "/api/users")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(user1)))
                .andExpect(jsonPath("$[1].id", isIdOf(user2)));
    }

    @Test
    public void createUser() throws Exception {
        user1 = givenUser(user1);
        final UserIn newUser = UserData.newUser();

        crt.allPostTests(user1, "users:create", "/api/users", json(newUser))
                .andExpect(jsonPath("$.firstName", is(newUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(newUser.getLastName())))
                .andExpect(jsonPath("$.email", is(newUser.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/users", json(UserData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]", isValidationError("email", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[1]", isValidationError("firstName", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[2]", isValidationError("lastName", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[3]", isValidationError("password", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/users", json(UserData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]", isValidationError("email", ApiString.EMAIL_INVALID)))
                .andExpect(jsonPath("$[1]", isValidationError("firstName", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[2]", isValidationError("lastName", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[3]", isValidationError("password", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateUser() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);

        String jsonData = mapToJson(data -> {
            data.put("email", "is.updated@b.c");
            data.put("firstName", "Användare");
            data.put("lastName", "Två");
            data.put("password", "newPassword");
        });

        crt.allPutTests(user1, "users:update", "/api/users", user2.getId(), jsonData)
                .andExpect(jsonPath("$.email", is("is.updated@b.c")))
                .andExpect(jsonPath("$.firstName", is("Användare")))
                .andExpect(jsonPath("$.lastName", is("Två")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void deleteUser() throws Exception {
        user1 = givenUser(user1);
        user2 = givenUser(user2);

        crt.allDeleteTests(user1, "users:delete", "/api/users", user2.getId());
    }
}
