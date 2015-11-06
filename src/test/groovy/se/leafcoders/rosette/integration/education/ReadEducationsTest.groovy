package se.leafcoders.rosette.integration.education

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest

public class ReadEducationsTest extends AbstractIntegrationTest {

    @Test
    public void successReadAll() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducation(eventEducation1)
        givenEducation(eventEducation2)
        givenPermissionForUser(user1, ["educations:read"])

        // When
        String getUrl = "/educations"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """[
            ${ toJSON(eventEducation1) },
            ${ toJSON(eventEducation2) }
		]"""
        thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void successReadAllWithoutPermissionButResultIsEmpty() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducation(eventEducation1)

        // When
        String getUrl = "/educations"
        HttpResponse getResponse = whenGet(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

        assertEquals("[]", responseBody)
    }
}
