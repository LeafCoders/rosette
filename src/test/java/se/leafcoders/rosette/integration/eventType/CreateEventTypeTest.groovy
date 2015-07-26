package se.leafcoders.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.DefaultSetting
import se.leafcoders.rosette.model.EventType;

public class CreateEventTypeTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["eventTypes:create", "*:read"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)

		// When
		String postUrl = "/eventTypes"
		DefaultSetting<Boolean> hasPublicEvents = new DefaultSetting<Boolean>(value: true, allowChange: true)
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "speakers",
			"name" : "Speakers",
			"description" : "Description",
			"hasPublicEvents" : ${ toJSON(hasPublicEvents) },
			"resourceTypes" : [ ${ toJSON(userResourceTypeSingle) }, ${ toJSON(uploadResourceTypeSingle) } ] 
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "speakers",
			"name" : "Speakers",
			"description" : "Description",
			"hasPublicEvents" : ${ toJSON(hasPublicEvents) },
			"resourceTypes" : [ ${ toJSON(userResourceTypeSingle) }, ${ toJSON(uploadResourceTypeSingle) } ] 
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenDataInDatabaseIs(EventType.class, "[${expectedData}]")
		thenItemsInDatabaseIs(EventType.class, 1)
    }
}
