package se.leafcoders.rosette.integration.education

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.education.Education
import se.leafcoders.rosette.model.education.EventEducation
import se.leafcoders.rosette.model.upload.UploadResponse
import com.mongodb.util.JSON


public class CreateEducationTest extends AbstractIntegrationTest {

    @Test
    public void createEventEducationWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(educationType1)

        givenUploadFolder(uploadFolderEducationThemes)
        UploadResponse image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)

        givenEvent(event1)
        givenPermissionForUser(user1, ["educations:create", "educationTypes:read", "educationThemes:read", "events:read"])

        // When
        String postUrl = "/educations"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
			"type" : "event",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef1) },
			"title" : "Education",
			"content" : "This is the content",
            "questions" : "Some questions",
            "event" : ${ toJSON(eventRef1) }
		}""")

        // Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

        String createdId = JSON.parse(responseBody)['id'];
        String expectedData = """{
            "type" : "event",
            "id" : "${ createdId }",
            "educationType" : {
                "id" : "${ educationTypeRef1.id }",
                "name" : "${ educationTypeRef1.name }"
            },
            "educationTheme" : {
                "id" : "${ educationThemeRef1.id }",
                "title" : "${ educationThemeRef1.title }"
            },
            "time" : "${ TestUtil.dateToModelTime(event1.startTime) }",
            "title" : "Education",
            "content" : "This is the content",
            "questions" : "Some questions",
            "recording" : null,
            "event" : {
                "id" : "${ eventRef1.id }",
                "title" : "${ eventRef1.title }",
                "startTime" : "2012-03-26 11:00 Europe/Stockholm"
            },
            "authorName" : "${ user1.fullName }",
            "updatedTime" : "${ TestUtil.dateToModelTime(readDb(EventEducation.class, createdId).updatedTime) }"
		}"""
        thenResponseDataIs(responseBody, expectedData)
        releasePostRequest()
        thenDataInDatabaseIs(Education.class, "[${expectedData}]")
        thenItemsInDatabaseIs(Education.class, 1)
    }

    @Test
    public void failsWhenCreateWithoutType() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenEducationType(educationType1)

        givenUploadFolder(uploadFolderEducationThemes)
        UploadResponse image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)

        givenEvent(event1)
        givenPermissionForUser(user1, ["educations:create", "educationTypes:read", "educationThemes:read", "events:read"])

        // When
        String postUrl = "/educations"
        HttpResponse postResponse = whenPost(postUrl, user1, """{
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef1) },
            "title" : "Education",
            "content" : "This is the content",
            "questions" : "Some questions",
            "event" : ${ toJSON(eventRef1) }
		}""")

        // Then
        thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
    }
}
