package se.leafcoders.rosette.integration.event

import java.io.IOException;
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.*
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil


public class ReadEventsTest extends AbstractIntegrationTest {

	@Test
	public void readEventsWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event1)
		givenEvent(event2)
		givenPermissionForUser(user1, ["events:read"])

		// When
		String getUrl = "/events"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		thenResponseDataIs(responseBody, """[${ toJSON(event1) }, ${ toJSON(event2) }]""")
	}

	@Test
	public void readEventsBetweenDates() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event1)
		givenEvent(event2)
		givenPermissionForUser(user1, ["events:read"])
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd")

		// When
		String getUrl = "/events?from=${ df.format(event1.startTime) }&before=${ df.format(event2.startTime) }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		// Should only contain event1 because 'before' day shall not be included
		thenResponseDataIs(responseBody, """[${ toJSON(event1) }]""")
	}

    @Test
    public void readEventsWithResourceTypePermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenLocation(location1)
        givenEventType(eventType1)
        givenResourceType(userResourceTypeSingle)
        givenResourceType(uploadResourceTypeSingle)
        givenEvent(event1)

        // When
        String getUrl = "/events"
        HttpResponse getResponseEmpty = whenGet(getUrl, user1)

        // Then
        String responseEmptyBody = thenResponseCodeIs(getResponseEmpty, HttpServletResponse.SC_OK)
        thenResponseDataIs(responseEmptyBody, "[]")

        // When
        givenPermissionForUser(user1, ["events:read:resourceTypes:${ userResourceTypeSingle.id }"])
        resetAuthCaches()
        HttpResponse getResponseOne = whenGet(getUrl, user1)

        // Then
        String responseOneBody = thenResponseCodeIs(getResponseOne, HttpServletResponse.SC_OK)
        thenResponseDataIs(responseOneBody, "[${ toJSON(event1) }]")
    }
}
