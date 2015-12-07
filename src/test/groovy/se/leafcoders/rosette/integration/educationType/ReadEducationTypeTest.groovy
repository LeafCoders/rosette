package se.leafcoders.rosette.integration.educationType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UserResourceType

public class ReadEducationTypeTest extends AbstractIntegrationTest {

    @Test
    public void successReadOne() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducationType(educationType1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:read:${ educationType1.id }"])

        // When
        String getUrl = "/educationTypes/${ educationType1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
            "id" : "letters",
            "name" : "Letters",
            "description" : "Letters about life",
            "authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) },
            "uploadFolder" : ${ toJSON(uploadFolderEducations) }
		}"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void failReadNotFound() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenPermissionForUser(user1, ["educationTypes:read"])

        // When
        String getUrl = "/educationTypes/nonExistingKey"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
    }

    @Test
    public void failReadWithoutPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(educationType1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)

        // When
        String getUrl = "/educationTypes/${ educationType1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
