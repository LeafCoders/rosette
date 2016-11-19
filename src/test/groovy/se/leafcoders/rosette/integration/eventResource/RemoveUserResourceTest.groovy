package se.leafcoders.rosette.integration.eventResource

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event

public class RemoveUserResourceTest extends AbstractIntegrationTest {

	@Test
	public void removeUserResourceWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
        givenResourceType(userResourceTypeSingle)
        givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, ["events:delete:resourceTypes", "resourceTypes:read"])

		// When
		String deleteUrl = "/events/${ event1.id }/resources/${ userResourceTypeSingle.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenDataInDatabaseIs(Event, event1.id, { Event event -> return event.resources.any { it.type == "user" } }, 'false')
    }

	@Test
	public void failToRemoveUserResourceWhenNotExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType2)
		givenResourceType(userResourceTypeSingle)
		givenEvent(event3)
		givenPermissionForUser(user1, ["events:delete:resourceTypes"])
		
		// When
		String deleteUrl = "/events/${ event3.id }/resources/${ userResourceTypeSingle.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "resource.doesNotExists" }
		]""")
	}
	
	@Test
	public void failToRemoveUserResourceWhenMissingPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenGroup(group1)
        givenLocation(location1)
        givenEventType(eventType1)
        givenResourceType(userResourceTypeSingle)
        givenResourceType(uploadResourceTypeSingle)
        givenEvent(event1)
        givenPermissionForUser(user1, ["events:create:resourceTypes", "resourceTypes:read"])

        // When
        String deleteUrl = "/events/${ event1.id }/resources/${ userResourceTypeSingle.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, toJSON([
            error: "error.forbidden",
            reason: "error.missingPermission",
            reasonParams: [
                "events:delete:" + event1.id + ",events:delete:resourceTypes:" + userResourceTypeSingle.id + ",events:delete:eventTypes:" + eventType1.id
            ]
        ]))
	}

}
