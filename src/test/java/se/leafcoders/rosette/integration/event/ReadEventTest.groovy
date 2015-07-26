package se.leafcoders.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.*
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil


public class ReadEventTest extends AbstractIntegrationTest {

	@Test
	public void readEventWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["events:read:${ event1.id }"])
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)

		// When
		String getUrl = "/events/${ event1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		thenResponseDataIs(responseBody, """{
			"id" : "${ event1.id }",
			"eventType" : ${ toJSON(eventType1) },
			"title" : "An event",
			"startTime" : "2012-03-26 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 12:00 Europe/Stockholm",
			"description" : "Description...",
			"location" : { "ref" : ${ toJSON(location1) }, "text" : null },
			"isPublic" : true,
			"resources" : [
				{
					"type" : "user",
					"resourceType" : ${ toJSON(userResourceTypeSingle) },
					"users" : {	"refs" : [ ${ toJSON(userRef1) } ],	"text" : null }
				},
				{
					"type" : "upload",
					"resourceType" : ${ toJSON(uploadResourceTypeSingle) },
					"uploads" : []
				}
			]
		}""")
	}

	@Test
	public void readEventWithEventTypePermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)

		// When
		String getUrl = "/events/${ event1.id }"
		HttpResponse getResponseFail = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponseFail, HttpServletResponse.SC_FORBIDDEN)

		// When
		givenPermissionForUser(user1, ["events:read:eventTypes:${ eventType1.id }"])
		resetAuthCaches()
		HttpResponse getResponseSuccess = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponseSuccess, HttpServletResponse.SC_OK)
	}

	@Test
	public void readEventWithResourceTypePermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)

		// When
		String getUrl = "/events/${ event1.id }"
		HttpResponse getResponseFail = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponseFail, HttpServletResponse.SC_FORBIDDEN)

		// When
		givenPermissionForUser(user1, ["events:read:resourceTypes:${ userResourceTypeSingle.id }"])
		resetAuthCaches()
		HttpResponse getResponseSuccess = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponseSuccess, HttpServletResponse.SC_OK)
	}

	@Test
	public void failReadEventThatDontExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:*"])
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)

		// When
		String getUrl = "/events/${ event1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
	}
}
