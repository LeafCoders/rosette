package se.leafcoders.rosette.integration.educationTheme

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.upload.UploadResponse

public class ReadEducationThemesTest extends AbstractIntegrationTest {

    @Test
    public void successReadAll() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadResponse image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)
        givenPermissionForUser(user1, ["educationThemes:read"])

        // When
        String getUrl = "/educationThemes"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """[
            ${ toJSON(educationTheme2) },
            ${ toJSON(educationTheme1) }
		]"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void successReadAllWithoutPermissionButResultIsEmpty() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadResponse image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        
        // When
        String getUrl = "/educationThemes"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        assertEquals("[]", responseBody)
    }
}
