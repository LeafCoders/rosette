package se.leafcoders.rosette.integration.educationType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EducationType


public class UpdateEducationTypeTest extends AbstractIntegrationTest {

    @Test
    public void updateEducationTypeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(educationType1)
        givenResourceType(userResourceTypeSingle)
        givenResourceType(userResourceTypeMultiAndText)
        givenEventType(eventType1)
        givenEventType(eventType2)
        givenPermissionForUser(user1, ["educationTypes:read,update:${ educationType1.id }", "resourceTypes:read", "eventTypes:read"])

        // When
        String putUrl = "/educationTypes/${ educationType1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id" : "willNotBeChanged",
			"name": "New name",
			"description": "New description",
            "authorResourceType" : ${ toJSON(userResourceTypeMultiAndText) },
            "eventType" : ${ toJSON(eventType2) }
		}""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
			"id" : "${ educationType1.id }",
            "name": "New name",
            "description": "New description",
            "authorResourceType" : ${ toJSON(userResourceTypeMultiAndText) },
            "eventType" : ${ toJSON(eventType2) }
		}]"""
        releasePutRequest()
        thenDataInDatabaseIs(EducationType.class, expectedData)
        thenItemsInDatabaseIs(EducationType.class, 1)
    }
}
