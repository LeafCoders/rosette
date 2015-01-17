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
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// When
		String putUrl = "/eventTypes/${eventType2.id}"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "willNotChange",
			"name" : "Changed name",
			"description" : "New description",
			"showOnPalmate" : true,
			"resourceTypes" : [
				{ "idRef": "${uploadResourceTypeSingle.id}" },
				{ "idRef": "${userResourceTypeSingle.id}" }
			]
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
		String expectedData = """[
			{
				"id" : "${eventType1.id}",
				"name" : "EventType 1",
				"description" : "Description...",
				"showOnPalmate" : true,
				"resourceTypes" : [
					{ "idRef" : "${userResourceTypeSingle.id}", "referredObject" : null },
					{ "idRef" : "${uploadResourceTypeSingle.id}", "referredObject" : null }
				]
			},
			{
				"id" : "${eventType2.id}",
				"name" : "Changed name",
				"description" : "New description",
				"showOnPalmate" : true,
				"resourceTypes" : [
					{ "idRef" : "${uploadResourceTypeSingle.id}", "referredObject" : null },
					{ "idRef" : "${userResourceTypeSingle.id}", "referredObject" : null }
				]
			}
		]"""
		thenDataInDatabaseIs(EventType.class, expectedData)
		thenItemsInDatabaseIs(EventType.class, 2)
	}
}
