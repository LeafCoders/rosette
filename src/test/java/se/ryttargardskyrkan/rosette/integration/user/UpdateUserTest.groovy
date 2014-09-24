package se.ryttargardskyrkan.rosette.integration.user

import static org.junit.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.User

public class UpdateUserTest extends AbstractIntegrationTest {
	
	@Test
	public void updateUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:users:${ user1.id }"])

		// When
		String putUrl = "/users/${ user1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"username" : "stubbe",
			"firstName" : "Misse",
			"lastName" : "Bult",
			"status" : ""
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[
			{
				"id" : "${ user1.id }",
				"username" : "stubbe",
				"firstName" : "Misse",
				"lastName" : "Bult",
				"status" : "active",
				"password" : null,
			    "fullName" : "Misse Bult"
			}
		]"""
		thenDataInDatabaseIs(User.class, expectedData)
		thenItemsInDatabaseIs(User.class, 1)
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
