package se.ryttargardskyrkan.rosette.integration.signupUser;

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.SignupUser

public class DeleteSignupUserTest extends AbstractIntegrationTest {

	@Test
	public void deleteOneUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["delete:signupUsers:${ signupUser1.id }"])

		// When
		String deleteUrl = "/signupUsers/${ signupUser1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(SignupUser.class, 0)
	}

	@Test
	public void failWhenDeleteUserWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["delete:signupUser:4711"])

		// When
		String deleteUrl = "/signupUsers/${ signupUser1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(SignupUser.class, 1)
	}
}
