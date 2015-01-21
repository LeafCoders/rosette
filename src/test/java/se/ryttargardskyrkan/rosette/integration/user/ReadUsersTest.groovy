package se.ryttargardskyrkan.rosette.integration.user

import static junit.framework.Assert.*
import java.io.IOException;
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.security.RosettePasswordService
import com.mongodb.util.JSON

public class ReadUsersTest extends AbstractIntegrationTest {
	
	@Test
	public void readAllUsersWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["read:users"])

		// When
		String getUrl = "/users"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ user1.id }",
				"username" : "user1",
				"firstName" : "User",
				"lastName" : "One",
				"email" : "u1@ser.se",
				"password" : null,
			    "fullName" : "User One"
			},
			{
				"id" : "${ user2.id }",
				"username" : "user2",
				"firstName" : "User",
				"lastName" : "Two",
				"email" : "u2@ser.se",
				"password" : null,
			    "fullName" : "User Two"
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
