package se.leafcoders.rosette.integration.education

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.education.EventEducation
import se.leafcoders.rosette.model.reference.EventRef
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.upload.UploadFile
import com.mongodb.util.JSON

public class ReadEducationTest extends AbstractIntegrationTest {

    @Test
    public void successReadOne() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        UploadFile educationRecording1 = givenUploadInFolder("educations", audioRecording1)
        givenEducation(eventEducation1, educationRecording1)
        givenPermissionForUser(user1, ["educations:read:${ eventEducation1.id }"])

        // When
        String getUrl = "/educations/${ eventEducation1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
            "type" : "event",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef1) },
            "id" : "${ JSON.parse(responseBody)['id'] }",
            "time" : "${ TestUtil.dateToModelTime(event1.startTime) }",
            "title" : "Education1",
            "content" : "Education1 content öäåè.",
            "questions" : "Education1 questions",
            "recording" : ${ toJSON(educationRecording1) },
            "event" : ${ toJSON(eventRef1) },
            "authorName" : "${ user1.fullName }",
            "updatedTime" : "${ TestUtil.dateToModelTime(readDb(EventEducation.class, eventEducation1.id).updatedTime) }"
		}"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void failReadNotFound() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))
        givenPermissionForUser(user1, ["educations:read"])

        // When
        String getUrl = "/educations/nonExistingKey"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
    }

    @Test
    public void failReadWithoutPermission() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))

        // When
        String getUrl = "/educations/${ eventEducation1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
