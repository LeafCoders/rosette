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
    public void createEducationTypeWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:create", "resourceTypes:read", "eventTypes:read", "uploadFolders:read:educations"])

        // When
        String postUrl = "/educationTypes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "bibleStudy",
			"name" : "Bibelstudium",
			"description" : "Undervisning om bibeln",
			"authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) },
            "uploadFolder" : ${ toJSON(uploadFolderEducations) }
		}""")

        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

        String expectedData = """{
			"id" : "bibleStudy",
            "name" : "Bibelstudium",
            "description" : "Undervisning om bibeln",
            "authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) },
            "uploadFolder" : ${ toJSON(uploadFolderEducations) }
		}"""
        thenResponseDataIs(responseBody, expectedData)
        releasePostRequest()
        thenDataInDatabaseIs(EducationType.class, "[${expectedData}]")
        thenItemsInDatabaseIs(EducationType.class, 1)
    }

    @Test
    public void failsWhenCreateWithoutUniqueId() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducationType(educationType1)
        givenResourceType(userResourceTypeSingle)
        givenEventType(eventType1)
        givenPermissionForUser(user1, ["educationTypes:create", "resourceTypes:read", "eventTypes:read", "uploadFolders:read:educations"])

        // When
        String postUrl = "/educationTypes"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "${ educationType1.id }",
            "name" : "Bibelstudium",
            "description" : "Undervisning om bibeln",
            "authorResourceType" : ${ toJSON(userResourceTypeSingle) },
            "eventType" : ${ toJSON(eventType1) },
            "uploadFolder" : ${ toJSON(uploadFolderEducations) }
		}""")

        // Then
        thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
    }
}
