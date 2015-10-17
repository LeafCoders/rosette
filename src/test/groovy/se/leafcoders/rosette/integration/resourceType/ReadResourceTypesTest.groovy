package se.leafcoders.rosette.integration.resourceType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UserResourceType
import com.mongodb.util.JSON

public class ReadResourceTypesTest extends AbstractIntegrationTest {

    @Test
    public void successReadAll() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["resourceTypes:read"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)

		// When
		String getUrl = "/resourceTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[{
			"id" : "posterFile",
			"type" : "upload",
			"name": "UploadResourceType Single",
			"description": "A poster file",
			"section" : "files",
			"multiSelect": false,
			"uploadFolder": ${ toJSON(uploadFolderPostersRef) }
		}, {
			"id" : "speaker",
			"type" : "user",
			"name": "UserResourceType Single",
			"description": "Description here",
			"section" : "persons",
			"multiSelect": false,
			"allowText": false,
			"group": ${ toJSON(group1) }
		}]"""
		thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void successReadAllWithoutPermissionButResultIsEmpty() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)

		// When
		String getUrl = "/resourceTypes"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		assertEquals("[]", responseBody)
    }
}
