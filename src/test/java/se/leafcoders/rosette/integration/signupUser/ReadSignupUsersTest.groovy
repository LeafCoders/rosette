package se.leafcoders.rosette.integration.signupUser

import java.io.IOException;
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.security.RosettePasswordService
import com.mongodb.util.JSON

public class ReadSignupUsersTest extends AbstractIntegrationTest {
	
	@Test
	public void readAllUsersWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenSignupUser(signupUser1)
		givenPermissionForUser(user1, ["read:signupUsers"])

		// When
		String getUrl = "/signupUsers"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ signupUser1.id }",
				"email" : "u1@sign.se",
				"firstName" : "User",
				"lastName" : "One",
				"password" : null,
				"permissions" : "Perms for u1",
				"createdTime" : null
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
