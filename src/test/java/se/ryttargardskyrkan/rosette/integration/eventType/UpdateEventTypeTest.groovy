package se.ryttargardskyrkan.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.model.EventType;

public class UpdateEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:eventTypes"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// When
		String putUrl = "/eventTypes/${eventType2.id}"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"name" : "Changed name",
			"key" : "willNotChange",
			"resourceTypes" : [
				{ "idRef": "${uploadResourceType1.id}" },
				{ "idRef": "${userResourceType1.id}" }
			]
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
		String expectedData = """[
			{
				"id" : "${eventType1.id}",
				"key" : "people",
				"name" : "EventType 1",
				"description" : "Description...",
				"resourceTypes" : [
					{ "idRef" : "${userResourceType1.id}", "referredObject" : null },
					{ "idRef" : "${uploadResourceType1.id}", "referredObject" : null }
				]
			},
			{
				"id" : "${eventType2.id}",
				"key" : "groups",
				"name" : "Changed name",
				"description" : "Description...",
				"resourceTypes" : [
					{ "idRef" : "${uploadResourceType1.id}", "referredObject" : null },
					{ "idRef" : "${userResourceType1.id}", "referredObject" : null }
				]
			}
		]"""
		thenDataInDatabaseIs(EventType.class, expectedData)
		thenItemsInDatabaseIs(EventType.class, 2)
	}
}
