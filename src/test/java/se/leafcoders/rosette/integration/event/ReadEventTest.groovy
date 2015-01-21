package se.leafcoders.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.*
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil

@Ignore
public class ReadEventTest extends AbstractIntegrationTest {

	@Test
	public void readEventWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, [
			"read:events:${ event1.id }",
			"read:eventTypes",
			"read:locations",
			"read:users",
			"read:uploads",
			"read:resourceTypes"
		])
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
			"eventType" : {
				"idRef" : "${ eventType1.id }",
				"referredObject" : {
					"id" : "${ eventType1.id }",
					"name" : "EventType 1",
					"description" : "Description...",
					"resourceTypes": [
						{ "idRef" : "${ userResourceTypeSingle.id }", "referredObject" : null },
						{ "idRef" : "${ uploadResourceTypeSingle.id }", "referredObject" : null }
					]
				}
			},
			"title" : "An event",
			"startTime" : "2012-03-26 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 12:00 Europe/Stockholm",
			"description" : "Description...",
			"location" : {
				"idRef" : "${ location1.id }",
				"text" : null,
				"referredObject" : {
					"id" : "${ location1.id }",
					"name" : "Away",
					"description" : "Description...",
					"directionImage" : null
				}
			},
			"resources" : [
				{
					"type" : "user",
					"resourceType" : {
						"idRef" : "${ userResourceTypeSingle.id }",
						"referredObject" : {
							"type" : "user",
							"id" : "${ userResourceTypeSingle.id }",
							"name" : "UserResourceType Single",
							"description" : "Description here",
							"section" : "persons",
							"group" : { "idRef" : "${ group1.id }", "referredObject" : null },
							"multiSelect" : false,
							"allowText" : false
						}
					},
					"users" : {
						"refs" : [
							{
								"idRef" : "${ user1.id }",
								"referredObject" : {
									"id" : "${ user1.id }",
									"username" : "user1",
									"password" : null,
									"status" : "active",
									"firstName" : "User",
									"lastName" : "One",
									"fullName" : "User One"
								}
							}
						],
						"text" : null
					}
				},
				{
					"type" : "upload",
					"resourceType" : {
						"idRef" : "${ uploadResourceTypeSingle.id }",
						"referredObject" : {
							"type" : "upload",
							"id" : "${ uploadResourceTypeSingle.id }",
							"name" : "UploadResourceType Single",
							"description" : "A poster file",
							"section" : "files",
							"folderName" : "posters",
							"multiSelect" : false
						}
					},
					"uploads" : []
				}
			]
		}""")
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
