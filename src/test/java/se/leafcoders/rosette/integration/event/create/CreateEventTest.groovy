package se.leafcoders.rosette.integration.event.create

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.*
import org.springframework.data.mongodb.core.query.Query
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.security.RosettePasswordService
import com.mongodb.util.JSON

@Ignore
public class CreateEventTest extends AbstractIntegrationTest {

	@Test
	public void createWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:events", "read:eventTypes", "read:resourceTypes"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)

		// When
		postRequest = new HttpPost(baseUrl + "/events")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"eventType" : { "idRef" : "${eventType1.id}" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"eventType" : { "idRef" : "${eventType1.id}", "referredObject" : null },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : null,
			"location" : null,
			"resources" : [
				{
					"type" : "user",
					"resourceType" : { "idRef" : "${userResourceTypeSingle.id}", "referredObject" : null },
					"users" : { "refs" : null, "text" : null }
				}, {
					"type" : "upload",
					"resourceType" : { "idRef" : "${uploadResourceTypeSingle.id}", "referredObject" : null },
					"uploads" : []
				}
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
		postRequest.releaseConnection()
		thenItemsInDatabaseIs(Event.class, 1)
	}

	@Test
	public void failWhenCreateWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)

		// When
		postRequest = new HttpPost(baseUrl + "/events")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"eventType" : { "idRef" : "${eventType1.id}" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
	}

	@Test
	public void failWhenCreateWithoutFullPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:events", "read:eventTypes"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)

		// When
		postRequest = new HttpPost(baseUrl + "/events")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"eventType" : { "idRef" : "${eventType1.id}" },
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
	}
}