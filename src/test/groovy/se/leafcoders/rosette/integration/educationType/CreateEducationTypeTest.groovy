package se.leafcoders.rosette.integration.educationType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.education.EducationType


public class CreateEducationTypeTest extends AbstractIntegrationTest {

    @Test
    public void createEventEducationTypeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:create", "resourceTypes:read", "eventTypes:read"])

        // When
        String postUrl = "/educationTypes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
			"type" : "event",
			"id" : "bibleStudy",
			"name" : "Bibelstudium",
			"description" : "Undervisning om bibeln",
			"authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) }
		}""")

        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
			"type" : "event",
			"id" : "bibleStudy",
            "name" : "Bibelstudium",
            "description" : "Undervisning om bibeln",
            "authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) }
		}"""
        thenResponseDataIs(responseBody, expectedData)
        releasePostRequest()
        thenDataInDatabaseIs(EducationType.class, "[${expectedData}]")
        thenItemsInDatabaseIs(EducationType.class, 1)
    }

    @Test
    public void failsWhenCreateWithoutType() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:create", "resourceTypes:read", "eventTypes:read"])

        // When
        String postUrl = "/educationTypes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
            "id" : "bibleStudy",
            "name" : "Bibelstudium",
            "description" : "Undervisning om bibeln",
            "authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) }
		}""")

        // Then
        thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
    }

    @Test
    public void failsWhenCreateWithoutUniqueKey() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(eventEducationType1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:create", "resourceTypes:read", "eventTypes:read"])

        // When
        String postUrl = "/educationTypes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
            "type" : "event",
			"id" : "${ eventEducationType1.id }",
            "name" : "Bibelstudium",
            "description" : "Undervisning om bibeln",
            "authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) }
		}""")

        // Then
        thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
    }
}
