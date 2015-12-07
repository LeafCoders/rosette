package se.leafcoders.rosette.integration.education

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.Education

public class DeleteEducationTest extends AbstractIntegrationTest {

    @Test
    public void deleteEventEducationWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))
        givenPermissionForUser(user1, ["educations:delete:${ eventEducation1.id }"])

        // When
        String deleteUrl = "/educations/${ eventEducation1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(Education.class, 0)
    }

    @Test
    public void failsWhenNothingToDelete() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))
        givenPermissionForUser(user1, ["educations:delete"])

        // When
        String deleteUrl = "/educations/nonExistingKey"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_NOT_FOUND)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(Education.class, 1)
    }

    @Test
    public void failsWhenMissingPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))

        // When
        String deleteUrl = "/educations/${ educationType1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(Education.class, 1)
    }
}
