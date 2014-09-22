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

public class ReadResourceTypeTest extends AbstractIntegrationTest {

    @Test
    public void successReadOne() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:resourceTypes", "read:groups"])
		givenGroup(group1)
		givenResourceType(userResourceType1)

		// When
		String getUrl = "/resourceTypes/${userResourceType1.id}"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """{
			"type": "user",
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
		}"""
		thenResponseDataIs(responseBody, expectedData)
    }

    @Test
    public void failReadNotFound() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:resourceTypes", "read:groups"])

		// When
		String getUrl = "/resourceTypes/4711"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_NOT_FOUND)
    }

	@Test
    public void failReadWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceType1)

		// When
		String getUrl = "/resourceTypes/${userResourceType1.id}"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
