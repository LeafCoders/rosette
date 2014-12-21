package se.ryttargardskyrkan.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.EventType;

public class CreateEventTypeTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:eventTypes", "read:*"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)

		// When
		String postUrl = "/eventTypes"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"key" : "speakers",
			"name" : "Speakers",
			"description" : "Description",
			"showOnPalmate" : true,
			"resourceTypes" : [
				{ "idRef": "${userResourceType1.id}" },
				{ "idRef": "${uploadResourceType1.id}" }
			]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"key" : "speakers",
			"name" : "Speakers",
			"description" : "Description",
			"showOnPalmate" : true,
			"resourceTypes" : [
				{ "idRef" : "${userResourceType1.id}","referredObject" : null },
				{ "idRef" : "${uploadResourceType1.id}", "referredObject" : null }
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenDataInDatabaseIs(EventType.class, "[${expectedData}]")
		thenItemsInDatabaseIs(EventType.class, 1)
    }
}
