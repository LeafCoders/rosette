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
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)

		// When
		String postUrl = "/eventTypes"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "speakers",
			"name" : "Speakers",
			"description" : "Description",
			"showOnPalmate" : true,
			"resourceTypes" : [
				{ "idRef": "${userResourceTypeSingle.id}" },
				{ "idRef": "${uploadResourceTypeSingle.id}" }
			]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "speakers",
			"name" : "Speakers",
			"description" : "Description",
			"showOnPalmate" : true,
			"resourceTypes" : [
				{ "idRef" : "${userResourceTypeSingle.id}","referredObject" : null },
				{ "idRef" : "${uploadResourceTypeSingle.id}", "referredObject" : null }
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenDataInDatabaseIs(EventType.class, "[${expectedData}]")
		thenItemsInDatabaseIs(EventType.class, 1)
    }
}
