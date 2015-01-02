package se.ryttargardskyrkan.rosette.integration.signupUser

import static org.junit.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.SignupUser

public class UpdateSignupUserTest extends AbstractIntegrationTest {
	
	@Test
	public void updateUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["update:signupUsers:${ signupUser1.id }"])

		// When
		String putUrl = "/signupUsers/${ signupUser1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"username" : "stubbe",
			"firstName" : "Misse",
			"lastName" : "Bult",
			"email" : "new@ser.se",
			"permissions" : "New permissions"
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[
			{
				"id" : "${ signupUser1.id }",
				"username" : "stubbe",
				"firstName" : "Misse",
				"lastName" : "Bult",
				"email" : "new@ser.se",
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
		givenPermissionForUser(user1, ["update:users"])

		// When
		String putUrl = "/users/4711"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"username" : "stubbe",
			"firstName" : "Misse",
			"lastName" : "Bult",
			"status" : ""
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_NOT_FOUND)
	}
}
