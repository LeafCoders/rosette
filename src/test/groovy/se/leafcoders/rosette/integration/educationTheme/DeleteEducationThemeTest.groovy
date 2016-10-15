package se.leafcoders.rosette.integration.educationTheme

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EducationTheme
import se.leafcoders.rosette.model.upload.UploadFile

public class DeleteEducationThemeTest extends AbstractIntegrationTest {

    @Test
    public void deleteEducationThemeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenPermissionForUser(user1, ["educationThemes:delete:${ educationTheme1.id }"])

        // When
        String deleteUrl = "/educationThemes/${ educationTheme1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationTheme.class, 0)
    }

    @Test
    public void failsWhenNothingToDelete() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)
        givenPermissionForUser(user1, ["educationThemes:delete"])

        // When
        String deleteUrl = "/educationThemes/nonExistingKey"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_NOT_FOUND)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationTheme.class, 2)
    }

    @Test
    public void failsWhenMissingPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)

        // When
        String deleteUrl = "/educationThemes/${ educationTheme1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationTheme.class, 1)
    }

    @Test
    public void failsWhenReferencedByEducation() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder(uploadFolderEducationThemes.id, validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder(uploadFolderEducations.id, audioRecording1))
        givenPermissionForUser(user1, ["educationThemes:delete:${ educationTheme1.id }"])

        // When
        String deleteUrl = "/educationThemes/${ educationTheme1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, """{
            "error": "error.forbidden",
            "reason": "error.referencedBy",
            "reasonParams": ["EventEducation"]
        }""")

        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationTheme.class, 1)
    }
}
