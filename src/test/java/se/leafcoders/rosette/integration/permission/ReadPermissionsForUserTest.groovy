package se.leafcoders.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class ReadPermissionsForUserTest extends AbstractIntegrationTest {

	@Test
	public void readPermissionForUser() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		givenPermissionForEveryone(["events:read"])
		givenPermissionForUser(user1, ["permissions:read"])
		givenPermissionForGroup(group1, ["posters:read"])

		// When
		String getUrl = "/permissionsForUser"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			"events:read", "users:read:${user1.id}", "users:update:${user1.id}", "permissions:read",
			"posters:read", "users:read:${user1.id}"
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}

	
	@Test
	public void readPermissionForUserAfterChange() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)

		// When
		String getUrl = "/permissionsForUser"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			"users:read:${user1.id}", "users:update:${user1.id}"
		]"""
		thenResponseDataIs(responseBody, expectedData)
		
		// When
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		givenPermissionForEveryone(["events:read"])
		givenPermissionForUser(user1, ["permissions:read"])
		givenPermissionForGroup(group1, ["posters:read"])
		getResponse = whenGet(getUrl, user1)

		// Then
		responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		expectedData = """[
			"events:read", "users:read:${user1.id}", "users:update:${user1.id}", "permissions:read",
			"posters:read", "users:read:${user1.id}"
		]"""
		thenResponseDataIs(responseBody, expectedData)

	}

}
