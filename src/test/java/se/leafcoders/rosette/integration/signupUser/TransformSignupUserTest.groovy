package se.ryttargardskyrkan.rosette.integration.signupUser

import static org.junit.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.SignupUser
import se.ryttargardskyrkan.rosette.model.User

public class TransformSignupUserTest extends AbstractIntegrationTest {
	
	@Test
	public void transformSignupUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["read:signupUsers", "create:users", "delete:signupUsers"])

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
}
