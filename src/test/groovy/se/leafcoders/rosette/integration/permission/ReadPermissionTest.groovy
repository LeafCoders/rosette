package se.leafcoders.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class ReadPermissionTest extends AbstractIntegrationTest {

	@Test
	public void readPermissionAtUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		String everyonePermissionId = givenPermissionForEveryone(["events:read"])
		String userPermissionId = givenPermissionForUser(user1, ["permissions:read"])
		String groupPermissionId = givenPermissionForGroup(group1, ["posters:update:2", "locations:update"])

		// When
		String getUrl = "/permissions/${ groupPermissionId }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ groupPermissionId }",
			"everyone" : null,
			"user" : null,
			"group" : ${ toJSON(group1) },
			"patterns" : ["posters:update:2", "locations:update"]
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void readPermissionAtGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		String groupPermissionId = givenPermissionForGroup(group1, ["permissions:read"])
		String everyonePermissionId = givenPermissionForEveryone(["events:read"])

		// When
		String getUrl = "/permissions/${ everyonePermissionId }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ everyonePermissionId }",
			"everyone" : true,
			"user" : null,
			"group" : null,
			"patterns" : ["events:read"]
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
