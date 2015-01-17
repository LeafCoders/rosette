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
		givenResourceType(userResourceTypeSingle)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeSingle)
		givenResourceType(uploadResourceTypeMulti)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// When
		String getUrl = "/eventTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ eventType1.id }",
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
							"group" : { "idRef" : "${ group1.id }", "referredObject" : null }
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
			},
			{
				"id" : "${ eventType2.id }",
				"name" : "EventType 2",
				"description" : "Description...",
				"showOnPalmate" : false,
				"resourceTypes" : [
					{
						"idRef" : "${ userResourceTypeMultiAndText.id }",
						"referredObject" : {
							"type" : "user",
							"id" : "${ userResourceTypeMultiAndText.id }",
							"name" : "UserResourceType Multi",
							"description" : "Description here",
							"section" : "persons",
							"multiSelect" : true,
							"allowText" : true,
							"group" : { "idRef" : "${ group1.id }", "referredObject" : null }
						}
					},
					{
						"idRef" : "${ uploadResourceTypeMulti.id }",
						"referredObject" : {
							"type" : "upload",
							"id" : "${ uploadResourceTypeMulti.id }",
							"name" : "UploadResourceType Multi",
							"description" : "Some poster files",
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
