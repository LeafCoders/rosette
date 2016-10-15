package se.leafcoders.rosette.integration.education

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.education.EventEducation
import se.leafcoders.rosette.model.education.SimpleEducation
import se.leafcoders.rosette.model.reference.UserRefOrText
import se.leafcoders.rosette.model.upload.UploadFile


public class UpdateEducationTest extends AbstractIntegrationTest {

    @Test
    public void updateEventEducationWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        UploadFile educationRecording1 = givenUploadInFolder("educations", audioRecording1)
        givenEducation(eventEducation1, educationRecording1)
        givenEducationType(educationType1)
        givenEducationType(educationType2)
        
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)

        givenEvent(event1)
        givenEvent(event2)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation1.id }", "educationTypes:read", "educationThemes:read", "events:read", "uploads:read:educations"])

        // When
        String putUrl = "/educations/${ eventEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type": "event",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef2) },
            "title" : "Education1 new",
            "content" : "Education1 content new",
            "questions" : "Education1 questions new",
            "recording" : ${ toJSON(educationRecording1) },
            "event" : ${ toJSON(eventRef2) }
		}""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
            "type": "event",
            "id" : "${ eventEducation1.id }",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef2) },
            "time" : "${ TestUtil.dateToModelTime(event2.startTime) }",
            "title" : "Education1 new",
            "content" : "Education1 content new",
            "questions" : "Education1 questions new",
            "recording" : ${ toJSON(educationRecording1) },
            "event" : ${ toJSON(eventRef2) },
            "authorName" : null,
            "updatedTime" : "${ TestUtil.dateToModelTime(readDb(EventEducation.class, eventEducation1.id).updatedTime) }"
		}]"""
        releasePutRequest()
        thenDataInDatabaseIs(EventEducation.class, expectedData)
        thenItemsInDatabaseIs(EventEducation.class, 1)
    }

    @Test
    public void updateEventEducationWillSetAuthorName() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation2, null)
        givenEducationType(educationType1)
        givenEducationType(educationType2)

        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)

        givenEvent(event1)
        givenEvent(event2)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation2.id }", "educationTypes:read", "educationThemes:read", "events:read"])

        // When
        String putUrl = "/educations/${ eventEducation2.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
            "type": "event",
            "event" : ${ toJSON(eventRef1) }
        }""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
            "type": "event",
            "id" : "${ eventEducation2.id }",
            "educationType" : ${ toJSON(educationTypeRef2) },
            "educationTheme" : ${ toJSON(educationThemeRef1) },
            "time" : "${ TestUtil.dateToModelTime(event1.startTime) }",
            "title" : "${ eventEducation2.title }",
            "content" : "${ eventEducation2.content }",
            "questions" : "${ eventEducation2.questions }",
            "recording" : null,
            "event" : ${ toJSON(eventRef1) },
            "authorName" : "${ user1.fullName }",
            "updatedTime" : "${ TestUtil.dateToModelTime(readDb(EventEducation.class, eventEducation2.id).updatedTime) }"
        }]"""
        releasePutRequest()
        thenDataInDatabaseIs(EventEducation.class, expectedData)
        thenItemsInDatabaseIs(EventEducation.class, 1)
    }

    @Test
    public void updateSimpleEducationWillSetAuthorName() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUser(user2)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(simpleEducation1, null)
        givenEducationType(educationType1)
        givenEducationType(educationType2)

        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)

        givenPermissionForUser(user1, ["educations:read,update:${ simpleEducation1.id }", "users:read"])

        // When
        String putUrl = "/educations/${ simpleEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
            "type": "simple",
            "author" : ${ toJSON(new UserRefOrText(ref: userRef2)) }
        }""")

        // Then
        thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        String expectedData = """[{
            "type": "simple",
            "id" : "${ simpleEducation1.id }",
            "educationType" : ${ toJSON(educationTypeRef1) },
            "educationTheme" : ${ toJSON(educationThemeRef1) },
            "time" : "${ TestUtil.dateToModelTime(simpleEducation1.time) }",
            "title" : "${ simpleEducation1.title }",
            "content" : "${ simpleEducation1.content }",
            "questions" : "${ simpleEducation1.questions }",
            "recording" : null,
            "author" : ${ toJSON(new UserRefOrText(ref: userRef2)) },
            "authorName" : "${ userRef2.fullName }",
            "updatedTime" : "${ TestUtil.dateToModelTime(readDb(EventEducation.class, simpleEducation1.id).updatedTime) }"
        }]"""
        releasePutRequest()
        thenDataInDatabaseIs(SimpleEducation.class, expectedData)
        thenItemsInDatabaseIs(SimpleEducation.class, 1)
    }

    @Test
    public void changeOfEducationTypeShouldFail() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))
        givenEducationType(educationType1)
        givenEducationType(educationType2)
        
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)

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

    @Test
    public void changeToEventOfWrongTypeShouldFail() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderEducations)
        givenEducation(eventEducation1, givenUploadInFolder("educations", audioRecording1))
        givenEducationType(educationType1)
        givenEducationType(educationType2)
        
        givenUploadFolder(uploadFolderEducationThemes)
        UploadFile image = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, image)
        givenEducationTheme(educationTheme2, image)

        givenEvent(event1)
        givenEvent(event3)
        givenPermissionForUser(user1, ["educations:read,update:${ eventEducation1.id }", "educationTypes:read", "educationThemes:read", "events:read"])

        // When
        String putUrl = "/educations/${ eventEducation1.id }"
        HttpResponse putResponse = whenPut(putUrl, user1, """{
            "type": "event",
            "event" : ${ toJSON(eventRef3) }
        }""")

        // Then
        String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
        thenResponseDataIs(responseBody, """[
            { "property" : "education", "message" : "education.eventType.notAcceptedType" }
        ]""")
    }
}
