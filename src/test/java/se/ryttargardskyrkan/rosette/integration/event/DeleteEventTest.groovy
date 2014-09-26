package se.ryttargardskyrkan.rosette.integration.event;

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.event.Event

public class DeleteEventTest extends AbstractIntegrationTest {

	@Test
	public void deleteEventWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:events:${ event2.id }"])
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)
		givenEvent(event1)
		givenEvent(event2)
		
		// When
		String deleteUrl = "/events/${ event2.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Event.class, 1)
	}

	@Test
	public void failDeleteEventWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:events:invalid"])
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)
		givenEvent(event1)
		givenEvent(event2)
		
		// When
		String deleteUrl = "/events/${ event2.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(Event.class, 2)
	}
}
