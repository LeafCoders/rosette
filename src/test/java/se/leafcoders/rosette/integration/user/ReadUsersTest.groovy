package se.leafcoders.rosette.integration.user

import static junit.framework.Assert.*
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
				"email" : "u1@ser.se",
				"firstName" : "User",
				"lastName" : "One",
				"password" : null,
			    "fullName" : "User One"
			},
			{
				"id" : "${ user2.id }",
				"email" : "u2@ser.se",
				"firstName" : "User",
				"lastName" : "Two",
				"password" : null,
			    "fullName" : "User Two"
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void readUsersFromGroupMembership() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		givenGroupMembership(user2, group1)
		givenPermissionForUser(user1, ["read:groups"])

		// When
		String getUrl = "/users"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ user1.id }",
				"email" : "u1@ser.se",
				"firstName" : "User",
				"lastName" : "One",
				"password" : null,
			    "fullName" : "User One"
			},
			{
				"id" : "${ user2.id }",
				"email" : "u2@ser.se",
				"firstName" : "User",
				"lastName" : "Two",
				"password" : null,
			    "fullName" : "User Two"
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}

}
