package se.leafcoders.rosette.integration.eventResource

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event
import se.leafcoders.rosette.model.upload.UploadFile

public class UpdateUploadResourceTest extends AbstractIntegrationTest {

	@Test
	public void updateUploadResourceWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType2)
		givenUploadFolder(uploadFolderPosters)
		UploadFile image1 = givenUploadInFolder("posters", validPNGImage)
		UploadFile image2 = givenUploadInFolder("posters", validJPEGImage)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event2)
		givenPermissionForUser(user1, [
			"events:update:resourceTypes:${ uploadResourceTypeMulti.id }",
			"uploads:read:posters"
		])

		// When
		String putUrl = "/events/${ event2.id }/resources/${ uploadResourceTypeMulti.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : ${ toJSON(uploadResourceTypeMulti) },
			"uploads" : [ ${ toJSON(image1) }, ${ toJSON(image2) } ]
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
		thenDataInDatabaseIs(Event, event2.id,
			{ Event event -> return event.resources.find { it.type == "upload" } },	"""{
			"type" : "upload",
			"resourceType" : ${ toJSON(uploadResourceTypeMulti) },
			"uploads" : [ ${ toJSON(image1) }, ${ toJSON(image2) } ]
		}""")
	}

	@Test
	public void failToUpdateUploadWhenNotInFolder() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, ["events:update:resourceTypes:${ uploadResourceTypeSingle.id }"])
		
		UploadFile image1 = new UploadFile(
			id: getObjectId(),
			fileName: "anyName",
			folderId: "someFolder",
			fileUrl: "/fileName.jpg",
			mimeType: "image/jpg"
		)
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ uploadResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : ${ toJSON(uploadResourceTypeSingle) },
			"uploads" : [ ${ toJSON(image1) } ]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "uploadResource.uploadDoesNotExistInFolder" }
		]""")
	}

	@Test
	public void failToUpdateUploadsWhenNotAllowingMultiSelect() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenUploadFolder(uploadFolderPosters)
		UploadFile image1 = givenUploadInFolder("posters", validPNGImage)
		UploadFile image2 = givenUploadInFolder("posters", validJPEGImage)
		givenEvent(event1)
		givenPermissionForUser(user1, [
			"events:update:resourceTypes:${ uploadResourceTypeSingle.id }"
		])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ uploadResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : ${ toJSON(uploadResourceTypeSingle) },
			"uploads" : [ ${ toJSON(image1) }, ${ toJSON(image2) } ]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "uploadResource.multiUploadsNotAllowed" }
		]""")
	}
}
