package se.leafcoders.rosette.integration.signupUser

import static org.junit.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.SignupUser
import se.leafcoders.rosette.model.User
import com.mongodb.util.JSON

public class TransformSignupUserTest extends AbstractIntegrationTest {
	
	@Test
	public void transformSignupUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["signupUsers:read", "users:create", "signupUsers:delete"])

        // When
		String postUrl = "/signupUsersTransform/${ signupUser1.id }"
		HttpResponse postResponse = whenPost(postUrl, user1, "")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(SignupUser.class, 0)
		thenItemsInDatabaseIs(User.class, 2)
	}

	@Test
	public void failTransformSignupUserWithoutPermissions() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)

		// When
		String postUrl = "/signupUsersTransform/${ signupUser1.id }"
		HttpResponse postResponse = whenPost(postUrl, user1, "")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(SignupUser.class, 1)
		thenItemsInDatabaseIs(User.class, 1)
	}

    @Test
    public void transformSignupUserAndLogin() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenPermissionForUser(user1, ["signupUsers:*", "users:create"])

        // When Create SignupUser
        String postUrl = "/signupUsers"
        HttpResponse postResponse = whenPost(postUrl, null, """{
            "email" : "n@is.se",
            "firstName" : "Nisse",
            "lastName" : "Hult",
            "password" : "myOwnPassword",
            "permissions" : "All please"
        }""")
        
        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        String signupUserId = JSON.parse(responseBody)['id']

        // When Transform SignupUser
        postUrl = "/signupUsersTransform/${ signupUserId }"
        postResponse = whenPost(postUrl, user1, "")

        // Then
        thenResponseCodeIs(postResponse, HttpServletResponse.SC_OK)

        // When Login for new user
        HttpResponse loginResponse = whenLogin("n@is.se", "myOwnPassword")
        
        // Then
        thenResponseCodeIs(loginResponse, HttpServletResponse.SC_OK)
    }
}
