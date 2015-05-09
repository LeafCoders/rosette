package se.leafcoders.rosette.integration.authorization

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

public class AuthorizationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["events:read"])
		
		// When
		String getUrl = "/authorizations?permissions=events:read,events:update,%20events:read"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"events:read" : true,
		    "events:update" : false
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
