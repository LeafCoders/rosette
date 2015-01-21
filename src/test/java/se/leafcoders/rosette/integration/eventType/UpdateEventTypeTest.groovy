package se.leafcoders.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.model.EventType;

public class UpdateEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:eventTypes", "read:eventTypes", "read:resourceTypes", "read:groups"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeSingle)
		givenResourceType(uploadResourceTypeMulti)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// When
		String putUrl = "/eventTypes/${eventType2.id}"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"name" : "Changed name",
			"description" : "New description",
			"showOnPalmate" : true,
			"resourceTypes" : [ ${ toJSON(userResourceTypeSingle) }, ${ toJSON(uploadResourceTypeSingle) } ] 
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
		String expectedData = """[
			{
				"id" : "${eventType1.id}",
				"name" : "EventType 1",
				"description" : "Description...",
				"showOnPalmate" : true,
				"resourceTypes" : [ ${ toJSON(userResourceTypeSingle) }, ${ toJSON(uploadResourceTypeSingle) } ] 
			},
			{
				"id" : "${eventType2.id}",
				"name" : "Changed name",
				"description" : "New description",
				"showOnPalmate" : true,
				"resourceTypes" : [ ${ toJSON(userResourceTypeSingle) }, ${ toJSON(uploadResourceTypeSingle) } ] 
			}
		]"""
		thenDataInDatabaseIs(EventType.class, expectedData)
		thenItemsInDatabaseIs(EventType.class, 2)
	}
}
