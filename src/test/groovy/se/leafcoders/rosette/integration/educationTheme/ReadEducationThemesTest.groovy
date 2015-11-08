package se.leafcoders.rosette.integration.educationTheme

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UserResourceType

public class ReadEducationThemesTest extends AbstractIntegrationTest {

    @Test
    public void successReadAll() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationTheme(educationTheme1)
        givenEducationTheme(educationTheme2)
        givenPermissionForUser(user1, ["educationThemes:read"])

        // When
        String getUrl = "/educationThemes"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """[
            ${ toJSON(educationTheme1) },
            ${ toJSON(educationTheme2) }
		]"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void successReadAllWithoutPermissionButResultIsEmpty() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationTheme(educationTheme1)
        
        // When
        String getUrl = "/educationThemes"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        assertEquals("[]", responseBody)
    }
}
