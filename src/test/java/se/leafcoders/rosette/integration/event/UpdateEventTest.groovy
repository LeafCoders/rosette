package se.leafcoders.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.*
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event

public class UpdateEventTest extends AbstractIntegrationTest {

	@Test
	public void updateEventWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, ["events:read,update:${ event1.id }", "locations:read", "eventTypes:read"])

		// When
		String putUrl = "/events/${ event1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "${ event1.id }",
			"eventType" : ${ toJSON(eventType1) },
			"title" : "New title",
			"startTime" : "2015-05-05 05:00 Europe/Stockholm",
			"endTime" : "2015-05-05 06:00 Europe/Stockholm",
			"description" : "New description",
			"location" : { "ref" : ${ toJSON(location1) } },
			"isPublic" : false
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[{
			"id" : "${ event1.id }",
			"eventType" : ${ toJSON(eventType1) },
			"title" : "New title",
			"startTime" : "2015-05-05 05:00 Europe/Stockholm",
			"endTime" : "2015-05-05 06:00 Europe/Stockholm",
			"description" : "New description",
			"location" : { "ref" : ${ toJSON(location1) }, "text" : null },
			"isPublic" : false,
			"version" : 2,
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
		}]"""
		thenDataInDatabaseIs(Event.class, expectedData)
		thenItemsInDatabaseIs(Event.class, 1)
	}

	@Test
	public void isPublicShallNotBeChangable() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType2)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event3)
		givenPermissionForUser(user1, ["events:read,update:${ event3.id }", "locations:read", "eventTypes:read"])

		assert(event3.isPublic == false)
		assert(eventType2.hasPublicEvents.value == false)
		assert(eventType2.hasPublicEvents.allowChange == false)

		// When
		String putUrl = "/events/${ event3.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "${ event3.id }",
			"eventType" : ${ toJSON(eventType2) },
			"title" : "New title",
			"startTime" : "2015-05-05 05:00 Europe/Stockholm",
			"endTime" : "2015-05-05 06:00 Europe/Stockholm",
			"description" : "New description",
			"isPublic" : true,
			"version": 4711
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[{
			"id" : "${ event3.id }",
			"eventType" : ${ toJSON(eventType2) },
			"title" : "New title",
			"startTime" : "2015-05-05 05:00 Europe/Stockholm",
			"endTime" : "2015-05-05 06:00 Europe/Stockholm",
			"description" : "New description",
			"location" : null,
			"isPublic" : false,
			"version": 2,
			"resources" : []
		}]"""
		thenDataInDatabaseIs(Event.class, expectedData)
		thenItemsInDatabaseIs(Event.class, 1)
	}

	@Test
	public void changeOfEventTypeShouldFail() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenEventType(eventType2)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, ["events:read,update:${ event1.id }", "locations:read", "eventTypes:read"])

		// When
		String putUrl = "/events/${ event1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "${ event1.id }",
			"eventType" : ${ toJSON(eventType2) }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "event", "message" : "event.eventType.notAllowedToChange" }
		]""")
	}

	@Test
	public void updateEventThatDontExistShouldFail() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenPermissionForUser(user1, ["events:read,update:${ event1.id }", "locations:read", "eventTypes:read"])

		// When
		String putUrl = "/events/${ event1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "${ event1.id }",
			"eventType" : ${ toJSON(eventType1) },
			"description" : "New description"
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_NOT_FOUND)
	}

}
