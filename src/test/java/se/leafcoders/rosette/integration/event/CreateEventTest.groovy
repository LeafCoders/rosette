package se.leafcoders.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.junit.*
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.DefaultSetting
import se.leafcoders.rosette.model.event.Event
import com.mongodb.util.JSON

public class CreateEventTest extends AbstractIntegrationTest {

	@Test
	public void createWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenEventType(eventType1)
		givenPermissionForUser(user1, ["events:create", "eventTypes:read"])

		// When
		String postUrl = "/events"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"eventType" : { "id" : "${ eventType1.id }" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"eventType" : ${ toJSON(eventType1) },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : null,
			"location" : null,
			"isPublic" : false,
			"resources" : null
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenItemsInDatabaseIs(Event.class, 1)
	}

	@Test
	public void isPublicShallNotBeChangable() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenEventType(eventType2)
		givenPermissionForUser(user1, ["events:create", "eventTypes:read"])

		assert(eventType2.hasPublicEvents.value == false)
		assert(eventType2.hasPublicEvents.allowChange == false)
		
		// When
		String postUrl = "/events"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"eventType" : ${ toJSON(eventType2) },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"isPublic" : ${ !eventType2.hasPublicEvents.value }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"eventType" : ${ toJSON(eventType2) },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : null,
			"location" : null,
			"isPublic" : ${ eventType2.hasPublicEvents.value },
			"resources" : null
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenItemsInDatabaseIs(Event.class, 1)
	}

	@Test
	public void createWithEventTypePermissionSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenEventType(eventType1)
		givenPermissionForUser(user1, ["events:create:eventTypes:${ eventType1.id }", "eventTypes:read"])

		// When
		String postUrl = "/events"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"eventType" : { "id" : "${ eventType1.id }" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
	}

	@Test
	public void failWhenCreateWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)
		givenPermissionForUser(user1, ["eventTypes:read"])

		// When
		String postUrl = "/events"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"eventType" : { "id" : "${ eventType1.id }" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
		thenResponseDataIs(responseBody, """{
			"error" : "error.forbidden",
			"reason" : "error.missingPermission",
			"reasonParams" : ["events:create:eventTypes:${ eventType1.id },events:create"]
		}""")
	}

	@Test
	public void failWhenCreateWithoutFullPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)
		givenPermissionForUser(user1, ["events:create"])

		// When
		String postUrl = "/events"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"eventType" : { "id" : "${ eventType1.id }" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
		thenResponseDataIs(responseBody, """{
			"error" : "error.forbidden",
			"reason" : "error.missingPermission",
			"reasonParams" : ["eventTypes:read:${ eventType1.id }"]
		}""")
	}

	@Test
	public void createWithValidationErrors() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenEventType(eventType1)
		givenPermissionForUser(user1, ["events:create"])

		// When
		String postUrl = "/events"
		HttpResponse postResponse1 = whenPost(postUrl, user1, "{}")

		// Then
		String responseBody1 = thenResponseCodeIs(postResponse1, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse1, "Content-Type", "application/json;charset=UTF-8")
		thenResponseDataIs(responseBody1, """[
			{ "property" : "eventType", "message" : "event.eventType.notNull" },
			{ "property" : "startTime", "message" : "event.startTime.notNull" },
			{ "property" : "title", "message" : "event.title.notEmpty" }
		]""")

		releasePostRequest()
		thenItemsInDatabaseIs(Event.class, 0)
	}

	@Test
	public void createWithValidationErrorsForResources() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)
		givenPermissionForUser(user1, ["events:create", "eventTypes:read", "resourceTypes:read"])

		// When
		String postUrl = "/events"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"eventType" : { "id" : "${ eventType1.id }" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"resources" : [
				{
					"type" : "user",
					"resourceType" : ${ toJSON(userResourceTypeSingle) }  
				},
				{
					"type" : "upload",
					"resourceType" : ${ toJSON(uploadResourceTypeSingle) }  
				}
			]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")
		thenResponseDataIs(responseBody, """[
			{ "property" : "resources[1].uploads", "message" : "uploadResource.uploads.notNull" },
			{ "property" : "resources[0].users",   "message" : "userResource.users.notNull" }
		]""")
	}

}
