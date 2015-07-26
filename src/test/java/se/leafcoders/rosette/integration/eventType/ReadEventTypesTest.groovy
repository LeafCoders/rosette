package se.leafcoders.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.EventType;

public class ReadEventTypesTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["eventTypes:read", "resourceTypes:read", "groups:read"])
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
				"hasPublicEvents" : ${ toJSON(eventType1.hasPublicEvents) },
				"resourceTypes" : [ ${ toJSON(userResourceTypeSingle) }, ${ toJSON(uploadResourceTypeSingle) } ] 
			},
			{
				"id" : "${ eventType2.id }",
				"name" : "EventType 2",
				"description" : "Description...",
				"hasPublicEvents" : ${ toJSON(eventType2.hasPublicEvents) },
				"resourceTypes" : [ ${ toJSON(userResourceTypeMultiAndText) }, ${ toJSON(uploadResourceTypeMulti) } ] 
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
		releaseGetRequest()
		thenItemsInDatabaseIs(EventType.class, 2)
	}
}
