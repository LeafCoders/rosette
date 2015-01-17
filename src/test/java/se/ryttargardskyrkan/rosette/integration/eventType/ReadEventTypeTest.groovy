package se.ryttargardskyrkan.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.EventType;

public class ReadEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:eventTypes", "read:resourceTypes", "read:groups"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)

		// When
		String getUrl = "/eventTypes/${ eventType1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${eventType1.id}",
			"name" : "EventType 1",
			"description" : "Description...",
			"showOnPalmate" : true,
			"resourceTypes" : [
				{
					"idRef" : "${ userResourceTypeSingle.id }",
					"referredObject" : {
						"type" : "user",
						"id" : "${ userResourceTypeSingle.id }",
						"name" : "UserResourceType Single",
						"description" : "Description here",
						"section" : "persons",
						"multiSelect" : false,
						"allowText" : false,
						"group" : { "idRef" : "${group1.id}", "referredObject" : null }
					}
				},
				{
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
				}
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releaseGetRequest()
		thenItemsInDatabaseIs(EventType.class, 1)
	}
}
