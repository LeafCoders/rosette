package se.leafcoders.rosette.integration.education

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.reference.EventRef
import se.leafcoders.rosette.model.resource.ResourceType
import com.mongodb.util.JSON

public class ReadEducationTest extends AbstractIntegrationTest {

    @Test
    public void successReadOne() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducation(eventEducation1)
        givenPermissionForUser(user1, ["educations:read:${ eventEducation1.id }"])

        // When
        String getUrl = "/educations/${ eventEducation1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
            "type" : "event",
            "educationType" : ${ toJSON(eventEducationTypeRef1) },
            "id" : "${ JSON.parse(responseBody)['id'] }",
            "title" : "Education1",
            "content" : "Education1 content",
            "questions" : "Education1 questions",
            "event" : ${ toJSON(eventRef1) }
		}"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void failReadNotFound() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducation(eventEducation1)
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
        givenEducation(eventEducation1)

        // When
        String getUrl = "/educations/${ eventEducation1.id }"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
