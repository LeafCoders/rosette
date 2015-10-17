package se.leafcoders.rosette.integration.signupUser

import static org.junit.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.SignupUser

public class UpdateSignupUserTest extends AbstractIntegrationTest {
	
	@Test
	public void updateUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["signupUsers:update:${ signupUser1.id }", "signupUsers:read"])

		// When
		String putUrl = "/signupUsers/${ signupUser1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"email" : "new@ser.se",
			"firstName" : "Misse",
			"lastName" : "Bult",
			"permissions" : "New permissions"
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[
			{
				"id" : "${ signupUser1.id }",
				"email" : "new@ser.se",
				"firstName" : "Misse",
				"lastName" : "Bult",
				"password" : null,
				"permissions" : "New permissions",
				"createdTime" : null
			}
		]"""
		thenDataInDatabaseIs(SignupUser.class, expectedData)
		thenItemsInDatabaseIs(SignupUser.class, 1)
	}

	@Test
	public void failUpdateUserThatDontExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["signupUsers:update", "signupUsers:read"])

		// When
		String putUrl = "/signupUsers/4711"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"email" : "stubbe@stubbe.se",
			"firstName" : "Misse",
			"lastName" : "Bult"
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_NOT_FOUND)
	}
}
