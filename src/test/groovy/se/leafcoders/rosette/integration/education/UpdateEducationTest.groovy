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
        givenEducationType(educationType1)
        givenEducationType(educationType2)
        givenEducationTheme(educationTheme1)
        givenEducationTheme(educationTheme2)
        givenEvent(event1)
        givenEvent(event2)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation1.id }", "educationTypes:read", "educationThemes:read", "events:read"])

        // When
        String putUrl = "/educations/${ eventEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type": "event",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef2) },
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
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef2) },
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
        givenEducationType(educationType1)
        givenEducationType(educationType2)
        givenEducationTheme(educationTheme1)
        givenEducationTheme(educationTheme2)
        givenEvent(event1)
        givenEvent(event2)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation1.id }", "educationTypes:read", "educationThemes:read", "events:read"])

        // When
        String putUrl = "/educations/${ eventEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
            "type": "event",
            "educationType" : ${ toJSON(educationTypeRef2) }
        }""")

        // Then
        String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
        thenResponseDataIs(responseBody, """[
            { "property" : "education", "message" : "education.educationType.notAllowedToChange" }
        ]""")
    }
}
