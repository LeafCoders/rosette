package se.leafcoders.rosette.integration.educationType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UserResourceType

public class ReadEducationTypesTest extends AbstractIntegrationTest {

    @Test
    public void successReadAll() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)
        givenEducationType(eventEducationType2)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:read"])

        // When
        String getUrl = "/educationTypes"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """[
            ${ toJSON(eventEducationType1) },
            ${ toJSON(eventEducationType2) }
		]"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void successReadAllWithoutPermissionButResultIsEmpty() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)

        // When
        String getUrl = "/educationTypes"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        assertEquals("[]", responseBody)
    }
}
