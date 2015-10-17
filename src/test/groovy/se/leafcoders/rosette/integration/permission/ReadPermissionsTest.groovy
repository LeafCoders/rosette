package se.leafcoders.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class ReadPermissionsTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		String userPermissionId = givenPermissionForUser(user1, ["permissions:read"])
		String groupPermissionId = givenPermissionForGroup(group1, ["posters:update:2", "locations:update"])
		String everyonePermissionId = givenPermissionForEveryone(["events:read"])

		// When
		String getUrl = "/permissions"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ everyonePermissionId }",
				"everyone" : true,
				"user" : null,
				"group" : null,
				"patterns" : ["events:read"]
			},
			{
				"id" : "${ userPermissionId }",
				"everyone" : null,
				"user" : ${ toJSON(userRef1) },
				"group" : null,
				"patterns" : ["permissions:read"]
			},
			{
				"id" : "${ groupPermissionId }",
				"everyone" : null,
				"user" : null,
				"group" : ${ toJSON(group1) },
				"patterns" : ["posters:update:2", "locations:update"]
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
