package se.ryttargardskyrkan.rosette.integration.groupMembership

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadGroupMembershipsByGroupIdTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["read:groupMemberships", "read:users", "read:groups"])
		givenGroup(group1)
		givenGroup(group2)
		String groupMembId1 = givenGroupMembership(user1, group1)
		String groupMembId2 = givenGroupMembership(user2, group2)

		// When
		getRequest = new HttpGet(baseUrl + "/groupMemberships?groupId=${group2.id}")
		HttpResponse getResponse = whenGet(getRequest, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """[{
			"id" : "${groupMembId2}",
			"user" : {
				"idRef": "${user2.id}",
				"referredObject": {
					"id": "${user2.id}",
					"username" : "user2",
					"firstName" : "User",
					"lastName" : "Two",
					"fullName" : "User Two",
					"password" : null,
					"status" : "active"
				}
			},
			"group" : {
				"idRef": "${group2.id}",
				"referredObject": {
					"id": "${group2.id}",
					"name": "Users",
					"description": null
				}
			}
		}]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
