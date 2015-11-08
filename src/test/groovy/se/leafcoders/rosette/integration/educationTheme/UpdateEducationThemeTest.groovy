package se.leafcoders.rosette.integration.educationTheme

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EducationTheme
import se.leafcoders.rosette.model.education.EducationType


public class UpdateEducationThemeTest extends AbstractIntegrationTest {

    @Test
    public void updateEducationTypeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationTheme(educationTheme1)
        givenEducationType(educationType1)
        givenEducationType(educationType2)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationThemes:read,update:${ educationTheme1.id }", "educationTypes:read"])

        // When
        String putUrl = "/educationThemes/${ educationTheme1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "willNotBeChanged",
            "educationType" : ${ toJSON(educationTypeRef2) },
			"title": "Theme1 new",
			"content": "New content"
		}""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
			"id" : "${ educationTheme1.id }",
            "educationType" : ${ toJSON(educationTypeRef2) },
            "title": "Theme1 new",
            "content": "New content"
		}]"""
        releasePutRequest()
        thenDataInDatabaseIs(EducationTheme.class, expectedData)
        thenItemsInDatabaseIs(EducationTheme.class, 1)
    }
}
