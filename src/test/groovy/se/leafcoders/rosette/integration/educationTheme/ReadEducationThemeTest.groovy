package se.leafcoders.rosette.integration.educationTheme

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UserResourceType

public class ReadEducationThemeTest extends AbstractIntegrationTest {

    @Test
    public void successReadOne() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationTheme(educationTheme1)
        givenPermissionForUser(user1, ["educationThemes:read:${ educationTheme1.id }"])

        // When
        String getUrl = "/educationThemes/${ educationTheme1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
            "id" : "${ educationTheme1.id }",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "title" : "Theme1",
            "content" : "The theme 1 content"
		}"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void failReadNotFound() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenPermissionForUser(user1, ["educationThemes:read"])

        // When
        String getUrl = "/educationThemes/nonExistingKey"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
    }

    @Test
    public void failReadWithoutPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationTheme(educationTheme1)

        // When
        String getUrl = "/educationThemes/${ educationTheme1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
