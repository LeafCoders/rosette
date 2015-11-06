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
    public void updateEventEducationTypeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)
        givenResourceType(userResourceTypeSingle)
        givenResourceType(userResourceTypeMultiAndText)
        givenEventType(eventType1)
        givenEventType(eventType2)
        givenPermissionForUser(user1, ["educationTypes:read,update:${ eventEducationType1.id }", "resourceTypes:read", "eventTypes:read"])

        // When
        String putUrl = "/educationTypes/${ eventEducationType1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type": "event",
			"id" : "willNotBeChanged",
			"name": "New name",
			"description": "New description",
            "authorResourceType" : ${ toJSON(userResourceTypeMultiAndText) },
            "eventType" : ${ toJSON(eventType2) }
		}""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
            "type": "event",
			"id" : "${ eventEducationType1.id }",
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
