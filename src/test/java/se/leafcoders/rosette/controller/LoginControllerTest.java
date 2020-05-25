package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.Before;
import org.junit.Test;

import se.leafcoders.rosette.data.UserData;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.persistence.model.User;

public class LoginControllerTest extends AbstractControllerTest {

    private final CommonRequestTests crt = new CommonRequestTests(this, User.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void loginAsUser() throws Exception {
        final User superAdmin = UserData.superAdmin();
        givenUser(user1);
        givenUser(user2);
        givenUser(superAdmin);

        // Missing permission
        crt.postExpectForbidden(ApiError.MISSING_PERMISSION, user1, "/auth/loginAs/" + user2.getId(), "");

        // With permission for user2
        givenPermissionForUser(user1, "users:loginAs:" + user2.getId());

        // Missing permission
        crt.postExpectForbidden(ApiError.MISSING_PERMISSION, user1, "/auth/loginAs/999", "");

        // With permission for all users
        givenPermissionForUser(user1, "users:loginAs");

        // User does not exist
        crt.postExpectForbidden(ApiError.AUTH_USER_NOT_FOUND, user1, "/auth/loginAs/999", "");

        // User is super admin. Not allowed to login as a super admin
        crt.postExpectForbidden(ApiError.AUTH_USER_IS_SUPER_ADMIN, user1, "/auth/loginAs/" + superAdmin.getId(), "");

        // Success
        crt.postSuccessWithOk(user1, "/auth/loginAs/" + user2.getId(), "")
                .andExpect(jsonPath("$.id", is(user2.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(user2.getFullName())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())))
                .andExpect(header().exists("X-AUTH-TOKEN"));
    }

}
