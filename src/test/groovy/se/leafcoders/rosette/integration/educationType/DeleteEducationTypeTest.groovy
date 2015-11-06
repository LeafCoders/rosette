package se.leafcoders.rosette.integration.educationType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EducationType
import se.leafcoders.rosette.model.education.EventEducationType

public class DeleteEducationTypeTest extends AbstractIntegrationTest {

    @Test
    public void deleteEventEducationTypeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)
        givenPermissionForUser(user1, ["educationTypes:delete:${ eventEducationType1.id }"])

        // When
        String deleteUrl = "/educationTypes/${ eventEducationType1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationType.class, 0)
    }

    @Test
    public void failsWhenNothingToDelete() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)
        givenPermissionForUser(user1, ["educationTypes:delete"])

        // When
        String deleteUrl = "/educationTypes/nonExistingKey"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_NOT_FOUND)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationType.class, 1)
    }

    @Test
    public void failsWhenMissingPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)

        // When
        String deleteUrl = "/educationTypes/${ eventEducationType1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        releaseDeleteRequest()
        thenItemsInDatabaseIs(EducationType.class, 1)
    }
}
