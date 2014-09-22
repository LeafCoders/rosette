package se.ryttargardskyrkan.rosette.integration.resourceType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.resource.ResourceType
import se.ryttargardskyrkan.rosette.model.resource.UserResourceType
import com.mongodb.util.JSON

public class ReadResourceTypesTest extends AbstractIntegrationTest {

    @Test
    public void successReadAll() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:resourceTypes", "read:groups"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)

		// When
		String getUrl = "/resourceTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		Object responseObject = JSON.parse(responseBody);
		String expectedData = """[{
			"id": "${uploadResourceType1.id}",
			"key" : "posterFile",
			"name": "UploadResourceType 1",
			"description": "Select poster files",
			"section" : "files",
			"multiSelect": true,
			"folderName": "posters"
		}, {
			"id": "${userResourceType1.id}",
			"key" : "speaker",
			"name": "UserResourceType 1",
			"description": "Description here",
			"section" : "persons",
			"multiSelect": false,
			"allowText": false,
			"group": {
				"idRef": "${group1.id}",
				"referredObject": {
					"id": "${group1.id}",
					"name": "Admins",
					"description": null
				}
			}
		}]"""
		thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void successReadAllWithoutPermissionButResultIsEmpty() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceType1)

		// When
		String getUrl = "/resourceTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		assertEquals("[]", TestUtil.jsonFromResponse(getResponse))
    }

	@Test
    public void failReadAllWithoutGroupPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:resourceTypes"])
		givenGroup(group1)
		givenResourceType(userResourceType1)

		// When
		String getUrl = "/resourceTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
