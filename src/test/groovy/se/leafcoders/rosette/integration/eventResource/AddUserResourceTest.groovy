package se.leafcoders.rosette.integration.eventResource

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event

public class AddUserResourceTest extends AbstractIntegrationTest {

	@Test
	public void addUserResourceWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenGroupMembership(user1, group1)
		givenGroupMembership(user2, group1)
		givenLocation(location1)
		givenEventType(eventType2)
        givenResourceType(userResourceTypeMultiAndText)
		givenEvent(event3)
		givenPermissionForUser(user1, ["events:create:resourceTypes", "resourceTypes:read"])

		// When
		String postUrl = "/events/${ event3.id }/resources"
        String postData = toJSON([
            type: "user",
            resourceType: userResourceTypeMultiAndText,
            users: [
                refs: [userRef2],
                text: "Kalle Boll"
            ]
        ])
		HttpResponse postResponse = whenPost(postUrl, user1, postData)

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenDataInDatabaseIs(Event, event3.id, { Event event -> return event.resources.find { it.type == "user" } }, postData)
    }

	@Test
	public void failToAddUserResourceWhenAlreadyExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, ["events:create:resourceTypes"])
		
		// When
		String postUrl = "/events/${ event1.id }/resources"
        String postData = toJSON([
            type: "user",
            resourceType: userResourceTypeSingle,
            users: [
                refs: [userRef2]
            ]
        ])
		HttpResponse postResponse = whenPost(postUrl, user1, postData)

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "resource.alreadyExists" }
		]""")
	}
	
	@Test
	public void failToAddUserResourceWhenMissingPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUser(user2)
        givenGroup(group1)
        givenGroupMembership(user1, group1)
        givenGroupMembership(user2, group1)
        givenLocation(location1)
        givenEventType(eventType2)
        givenResourceType(userResourceTypeMultiAndText)
        givenEvent(event3)
        givenPermissionForUser(user1, ["events:update:resourceTypes", "resourceTypes:read"])

        // When
        String postUrl = "/events/${ event3.id }/resources"
        String postData = toJSON([
            type: "user",
            resourceType: userResourceTypeMultiAndText,
            users: [
                refs: [userRef2],
                text: "Kalle Boll"
            ]
        ])
        HttpResponse postResponse = whenPost(postUrl, user1, postData)

        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, toJSON([
            error: "error.forbidden",
            reason: "error.missingPermission",
            reasonParams: [
                "events:create:" + event3.id + ",events:create:resourceTypes:" + userResourceTypeMultiAndText.id + ",events:create:eventTypes:" + eventType2.id
            ]
        ]))
	}
}
