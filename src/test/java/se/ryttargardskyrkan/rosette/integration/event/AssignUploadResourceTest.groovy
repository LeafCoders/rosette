package se.ryttargardskyrkan.rosette.integration.event

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.event.Event

public class AssignUploadResourceTest extends AbstractIntegrationTest {

	@Test
	public void assignUploadResourceWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenLocation(location1)
		givenEventType(eventType2)
		Object image1 = givenUploadInFolder("posters", validPNGImage)
		Object image2 = givenUploadInFolder("posters", validJPEGImage)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event2)
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ uploadResourceTypeMulti.id }",
			"read:resourceTypes:${ uploadResourceTypeMulti.id }",
			"read:events:${ event2.id }"
		])

		// When
		String putUrl = "/events/${ event2.id }/resources/${ uploadResourceTypeMulti.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : { "idRef" : "${ uploadResourceTypeMulti.id }" },
			"uploads" : [ { "idRef" : "${ image1['id'] }" }, { "idRef" : "${ image2['id'] }" } ]
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
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ uploadResourceTypeSingle.id }",
			"read:resourceTypes:${ uploadResourceTypeSingle.id }",
			"read:events:${ event1.id }"
		])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ uploadResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : { "idRef" : "${ uploadResourceTypeSingle.id }" },
			"uploads" : [ { "idRef" : "fileThatDontExist" } ]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "uploadResource.assignedUploadNotInFolder" }
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
		Object image1 = givenUploadInFolder("posters", validPNGImage)
		Object image2 = givenUploadInFolder("posters", validJPEGImage)
		givenEvent(event1)
		givenPermissionForUser(user1, [
			"assign:resourceTypes:${ uploadResourceTypeSingle.id }",
			"read:resourceTypes:${ uploadResourceTypeSingle.id }",
			"read:events:${ event1.id }"
		])
		
		// When
		String putUrl = "/events/${ event1.id }/resources/${ uploadResourceTypeSingle.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type" : "upload",
			"resourceType" : { "idRef" : "${ uploadResourceTypeSingle.id }" },
			"uploads" : [ { "idRef" : "${ image1['id'] }" }, { "idRef" : "${ image2['id'] }" } ]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseDataIs(responseBody, """[
			{ "property" : "resource", "message" : "uploadResource.multiUploadsNotAllowed" }
		]""")
	}
}
