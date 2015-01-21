package se.leafcoders.rosette.integration.groupMembership

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class ReadGroupMembershipsTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["read:groupMemberships"])
		givenGroup(group1)
		String groupMembId1 = givenGroupMembership(user1, group1)
		String groupMembId2 = givenGroupMembership(user2, group1)
		
        // When
		String getUrl = "/groupMemberships"
		HttpResponse uploadResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(uploadResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[{
			"id" : "${groupMembId1}",
			"user" : ${ toJSON(user1) },
			"group" : ${ toJSON(group1) }
		}, {
			"id" : "${groupMembId2}",
			"user" : ${ toJSON(user2) },
			"group" : ${ toJSON(group1) }
		}]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
