package se.leafcoders.rosette.integration.educationTheme

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EducationTheme
import se.leafcoders.rosette.model.upload.UploadResponse
import com.mongodb.util.JSON


public class CreateEducationThemeTest extends AbstractIntegrationTest {

    @Test
    public void createEducationThemeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(educationType1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadResponse image = givenUploadInFolder("educationThemes", validPNGImage)
        givenPermissionForUser(user1, ["educationThemes:create", "educationTypes:read", "uploads:read"])

        // When
        String postUrl = "/educationThemes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
			"educationType" : ${ toJSON(educationTypeRef1) },
			"title" : "Theme title",
			"content" : "The content",
            "image" : ${ toJSON(image) }
		}""")

        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
            "id" : "${ JSON.parse(responseBody)['id'] }",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "title" : "Theme title",
            "content" : "The content",
            "image" : ${ toJSON(image) }
		}"""
        thenResponseDataIs(responseBody, expectedData)
        releasePostRequest()
        thenDataInDatabaseIs(EducationTheme.class, "[${expectedData}]")
        thenItemsInDatabaseIs(EducationTheme.class, 1)
    }

    @Test
    public void failWhenCreateWithInvalidContent() throws ClientProtocolException, IOException {
        givenUser(user1)
        givenEducationType(educationType1)
        givenPermissionForUser(user1, ["educationThemes:create", "educationTypes:read"])
        
        // When
        String postUrl = "/educationThemes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{}""")

        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
        thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """[
            { "property" : "educationType", "message" : "educationTheme.educationType.mustBeSet" },
            { "property" : "image",         "message" : "educationTheme.image.mustBeSet" },
            { "property" : "title",         "message" : "educationTheme.title.notEmpty" }
        ]"""

        thenResponseDataIs(responseBody, expectedData)
        thenItemsInDatabaseIs(EducationTheme.class, 0)
    }
}
