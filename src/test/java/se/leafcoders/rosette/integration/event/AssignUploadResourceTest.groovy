package se.leafcoders.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event
import se.leafcoders.rosette.model.upload.UploadResponse;

public class AssignUploadResourceTest extends AbstractIntegrationTest {

	@Test
	public void assignUploadResourceWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType2)
		givenUploadFolder(uploadFolderPosters)
		UploadResponse image1 = givenUploadInFolder("posters", validPNGImage)
		UploadResponse image2 = givenUploadInFolder("posters", validJPEGImage)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event2)
		givenPermissionForUser(user1, [
			"events:resourceTypes:update:${ uploadResourceTypeMulti.id }",
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
	}

	@Test
	public void failToAssignUploadWhenNotInFolder() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEvent(event1)
		givenPermissionForUser(user1, ["events:resourceTypes:update:${ uploadResourceTypeSingle.id }"])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ uploadResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : ${ toJSON(uploadResourceTypeSingle) },
			"uploads" : [ { "id" : "${ getObjectId() }" } ]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "uploadResource.uploadDoesNotExistInFolder" }
		]""")
	}

	@Test
	public void failToAssignUploadsWhenNotAllowingMultiSelect() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenUploadFolder(uploadFolderPosters)
		UploadResponse image1 = givenUploadInFolder("posters", validPNGImage)
		UploadResponse image2 = givenUploadInFolder("posters", validJPEGImage)
		givenEvent(event1)
		givenPermissionForUser(user1, [
			"events:resourceTypes:update:${ uploadResourceTypeSingle.id }"
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
