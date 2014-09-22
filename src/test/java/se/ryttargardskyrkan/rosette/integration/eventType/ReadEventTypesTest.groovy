package se.ryttargardskyrkan.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.EventType;

public class ReadEventTypesTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:eventTypes", "read:resourceTypes", "read:groups"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// When
		String getUrl = "/eventTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """[
			{
				"id" : "${eventType1.id}",
				"key" : "people",
				"name" : "EventType 1",
				"description" : "Description...",
				"resourceTypes" : [
					{
						"idRef" : "${userResourceType1.id}",
						"referredObject" : {
							"type" : "user",
							"id" : "${userResourceType1.id}",
							"key" : "speaker",
							"name" : "UserResourceType 1",
							"description" : "Description here",
							"section" : "persons",
							"multiSelect" : false,
							"allowText" : false,
							"group" : {
								"idRef" : "${group1.id}",
								"referredObject" : {
									"id" : "${group1.id}",
									"name" : "Admins",
									"description" : null
								}
							}
						}
					},
					{
						"idRef" : "${uploadResourceType1.id}",
						"referredObject" : {
							"type" : "upload",
							"id" : "${uploadResourceType1.id}",
							"key" : "posterFile",
							"name" : "UploadResourceType 1",
							"description" : "Select poster files",
							"section" : "files",
							"folderName" : "posters",
							"multiSelect" : true
						}
					}
				]
			},
			{
				"id" : "${eventType2.id}",
				"key" : "groups",
				"name" : "EventType 2",
				"description" : "Description...",
				"resourceTypes" : [
					{
						"idRef" : "${userResourceType1.id}",
						"referredObject" : {
							"type" : "user",
							"id" : "${userResourceType1.id}",
							"key" : "speaker",
							"name" : "UserResourceType 1",
							"description" : "Description here",
							"section" : "persons",
							"multiSelect" : false,
							"allowText" : false,
							"group" : {
								"idRef" : "${group1.id}",
								"referredObject" : {
									"id" : "${group1.id}",
									"name" : "Admins",
									"description" : null
								}
							}
						}
					},
					{
						"idRef" : "${uploadResourceType1.id}",
						"referredObject" : {
							"type" : "upload",
							"id" : "${uploadResourceType1.id}",
							"key" : "posterFile",
							"name" : "UploadResourceType 1",
							"description" : "Select poster files",
							"section" : "files",
							"folderName" : "posters",
							"multiSelect" : true
						}
					}
				]
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
		releaseGetRequest()
		thenItemsInDatabaseIs(EventType.class, 2)
	}
}
