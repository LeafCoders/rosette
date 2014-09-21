package se.ryttargardskyrkan.rosette.integration.groupMembership

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class ReadGroupMembershipTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["read:groupMemberships", "read:users", "read:groups"])
		givenGroup(group1)
		String groupMembId1 = givenGroupMembership(user1, group1)
		String groupMembId2 = givenGroupMembership(user2, group1)

        // When
        HttpGet getRequest = new HttpGet(baseUrl + "/groupMemberships/${groupMembId2}")
		HttpResponse uploadResponse = whenGet(getRequest, user1)

		// Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(uploadResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(uploadResponse)
		String expectedData = """{
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
				"idRef": "${group1.id}",
				"referredObject": {
					"id": "${group1.id}",
					"name": "Admins",
					"description": null
				}
			}
		}"""
		thenResponseDataIs(responseBody, expectedData)
		getRequest.releaseConnection()
	}
}
