package se.leafcoders.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event

public class AssignUserResourceTest extends AbstractIntegrationTest {

	@Test
	public void assignUserResourceWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenGroupMembership(user2, group1)
		givenLocation(location1)
		givenEventType(eventType2)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event2)
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ userResourceTypeMultiAndText.id }",
			"read:resourceTypes:${ userResourceTypeMultiAndText.id }",
			"read:events:${ event2.id }"
		])

		// When
		String putUrl = "/events/${ event2.id }/resources/${ userResourceTypeMultiAndText.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "user",
			"resourceType" : ${ toJSON(userResourceTypeMultiAndText) },
			"users" : {
				"refs" : [ ${ toJSON(user2) } ],
				"text" : "Kalle Boll"
			}
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
	}

	@Test
	public void failToAssignUserWhenNotInGroup() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ userResourceTypeSingle.id }",
			"read:resourceTypes:${ userResourceTypeSingle.id }",
			"read:events:${ event1.id }"
		])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ userResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "user",
			"resourceType" : ${ toJSON(userResourceTypeSingle) },
			"users" : {
				"refs" : [ { "id" : "userThatDontExist" } ]
			}
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "userResource.assignedUserNotInGroup" }
		]""")
	}
	
	@Test
	public void failToAssignUsersWhenNotAllowingMultiSelect() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		givenGroupMembership(user2, group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ userResourceTypeSingle.id }",
			"read:resourceTypes:${ userResourceTypeSingle.id }",
			"read:events:${ event1.id }"
		])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ userResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "user",
			"resourceType" : ${ toJSON(userResourceTypeSingle) },
			"users" : {
				"refs" : [ ${ toJSON(user1) }, ${ toJSON(user2) } ]
			}
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "userResource.multiUsersNotAllowed" }
		]""")

		// When
		HttpResponse putResponse2 = whenPut(putUrl, user1, """{
			"type" : "user",
			"resourceType" : ${ toJSON(userResourceTypeSingle) },
			"users" : {
				"refs" : [ ${ toJSON(user1) } ],
				"text" : "Someone"
			}
		}""")

		// Then
		String responseBody2 = thenResponseCodeIs(putResponse2, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody2, """[
			{ "property" : "resource", "message" : "userResource.multiUsersNotAllowed" }
		]""")
	}

	@Test
	public void failToAssignUserByTextWhenNotAllowed() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ userResourceTypeSingle.id }",
			"read:resourceTypes:${ userResourceTypeSingle.id }",
			"read:events:${ event1.id }"
		])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ userResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "user",
			"resourceType" : ${ toJSON(userResourceTypeSingle) },
			"users" : {
				"text" : "Someone"
			}
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "userResource.userByTextNotAllowed" }
		]""")
	}
}
