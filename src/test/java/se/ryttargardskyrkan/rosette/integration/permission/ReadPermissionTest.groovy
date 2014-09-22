package se.ryttargardskyrkan.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;

public class ReadPermissionTest extends AbstractIntegrationTest {

	@Test
	public void readPermissionAtUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		String userPermissionId = givenPermissionForUser(user1, ["read:permissions", "read:users", "read:groups"])
		String groupPermissionId = givenPermissionForGroup(group1, ["update:posters:2", "update:locations"])
		String everyonePermissionId = givenPermissionForEveryone(["read:events"])

		// When
		String getUrl = "/permissions/${ groupPermissionId }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """{
			"id" : "${ groupPermissionId }",
			"everyone" : null,
			"user" : null,
			"group" : {
				"idRef" : "${ group1.id }",
				"referredObject" : {
					"id" : "${ group1.id }",
					"name" : "Admins",
					"description" : null
				}
			},
			"patterns" : ["update:posters:2", "update:locations"]
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void readPermissionAtGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		String groupPermissionId = givenPermissionForGroup(group1, ["read:permissions", "read:users", "read:groups"])
		String everyonePermissionId = givenPermissionForEveryone(["read:events"])

		// When
		String getUrl = "/permissions/${ everyonePermissionId }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """{
			"id" : "${ everyonePermissionId }",
			"everyone" : true,
			"user" : null,
			"group" : null,
			"patterns" : ["read:events"]
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
