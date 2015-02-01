package se.leafcoders.rosette.integration.user

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil

public class ReadUserTest extends AbstractIntegrationTest {
	
	@Test
	public void readUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["read:users:${ user1.id }"])

		// When
		String getUrl = "/users/${ user1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ user1.id }",
			"email" : "u1@ser.se",
			"firstName" : "User",
			"lastName" : "One",
			"fullName": "User One",
			"password" : null
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void failWhenReadUserWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["read:users:${ user2.id }"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String getUrl = "/users/${ user1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
	}

	@Test
	public void failWhenReadUserThatDontExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:users"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String getUrl = "/users/${ user2.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
	}
}
