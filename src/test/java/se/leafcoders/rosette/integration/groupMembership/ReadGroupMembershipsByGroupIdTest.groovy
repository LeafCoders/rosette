package se.leafcoders.rosette.integration.groupMembership

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadGroupMembershipsByGroupIdTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["groupMemberships:read"])
		givenGroup(group1)
		givenGroup(group2)
		String groupMembId1 = givenGroupMembership(user1, group1)
		String groupMembId2 = givenGroupMembership(user2, group2)

		// When
		String getUrl = "/groupMemberships?groupId=${group2.id}"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[{
			"id" : "${groupMembId2}",
			"user" : ${ toJSON(userRef2) },
			"group" : ${ toJSON(group2) }
		}]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
