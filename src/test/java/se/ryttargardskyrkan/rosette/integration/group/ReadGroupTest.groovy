package se.ryttargardskyrkan.rosette.integration.group

import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;

public class ReadGroupTest extends AbstractIntegrationTest {

	@Test
	public void readGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:groups:${ group2.id }"])
		givenGroup(group1)
		givenGroup(group2)
		
		// When
		String getUrl = "/groups/${ group2.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """{
			"id" : "${ group2.id }",
			"name" : "Users",
			"description" : null
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void failReadGroupThatDontExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:groups"])
		givenGroup(group1)
		givenGroup(group2)
		
		// When
		String getUrl = "/groups/4711"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
	}
}
