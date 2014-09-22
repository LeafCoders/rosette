package se.ryttargardskyrkan.rosette.integration.location

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadLocationTest extends AbstractIntegrationTest {

	@Test
	public void readLocationWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:locations:${ location1.id }"])
		givenLocation(location1)
		givenLocation(location2)

		// When
		String getUrl = "/locations/${ location1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """{
			"id" : "${ location1.id }",
			"name" : "Away",
			"description" : "Description...",
			"directionImage" : null
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void failWhenReadLocationWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:locations:${ location1.id }"])
		givenLocation(location1)
		givenLocation(location2)

		// When
		String getUrl = "/locations/${ location2.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
	}
}
