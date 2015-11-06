package se.leafcoders.rosette.integration.education

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EventEducation


public class UpdateEducationTest extends AbstractIntegrationTest {

    @Test
    public void updateEventEducationWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducation(eventEducation1)
        givenEducationType(eventEducationType1)
        givenEducationType(eventEducationType2)
        givenEvent(event1)
        givenEvent(event2)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation1.id }", "educationTypes:read", "events:read"])

        // When
        String putUrl = "/educations/${ eventEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type": "event",
            "educationType" : ${ toJSON(eventEducationTypeRef1) },
            "title" : "Education1 new",
            "content" : "Education1 content new",
            "questions" : "Education1 questions new",
            "event" : ${ toJSON(eventRef2) }
		}""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
            "type": "event",
            "id" : "${ eventEducation1.id }",
            "educationType" : ${ toJSON(eventEducationTypeRef1) },
            "title" : "Education1 new",
            "content" : "Education1 content new",
            "questions" : "Education1 questions new",
            "event" : ${ toJSON(eventRef2) }
		}]"""
        releasePutRequest()
        thenDataInDatabaseIs(EventEducation.class, expectedData)
        thenItemsInDatabaseIs(EventEducation.class, 1)
    }

    @Test
    public void changeOfEducationTypeShouldFail() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducation(eventEducation1)
        givenEducationType(eventEducationType1)
        givenEducationType(eventEducationType2)
        givenEvent(event1)
        givenEvent(event2)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation1.id }", "educationTypes:read", "events:read"])

        // When
        String putUrl = "/educations/${ eventEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
            "type": "event",
            "educationType" : ${ toJSON(eventEducationTypeRef2) }
        }""")

        // Then
        String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
        thenResponseDataIs(responseBody, """[
            { "property" : "education", "message" : "education.educationType.notAllowedToChange" }
        ]""")
    }
}
