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
		givenPermissionForUser(user1, ["read:events"])
		
		// When
		String getUrl = "/authorizations?permissions=read:events,update:events,%20read:events"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"read:events" : true,
		    "update:events" : false
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
