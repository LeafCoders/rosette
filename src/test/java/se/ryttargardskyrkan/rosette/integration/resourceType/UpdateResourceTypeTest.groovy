package se.ryttargardskyrkan.rosette.integration.resourceType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.resource.ResourceType
import se.ryttargardskyrkan.rosette.model.resource.UserResourceType

public class UpdateResourceTypeTest extends AbstractIntegrationTest {

    @Test
    public void updateUserResourceTypeWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroup(group2)
		givenResourceType(userResourceTypeSingle)
		givenPermissionForUser(user1, ["update:resourceTypes:${userResourceTypeSingle.id}"])

		// When
		String putUrl = "/resourceTypes/${userResourceTypeSingle.id}"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"type": "user",
			"id" : "willNotBeChanged",
			"name": "UserResourceType Single New",
			"description": "Description here New",
			"section" : "users",
			"multiSelect": true,
			"allowText": true,
			"group": {
				"idRef": "${group2.id}"
			}
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
		String expectedData = """[{
			"id" : "${userResourceTypeSingle.id}",
			"type" : "user",
			"name": "UserResourceType Single New",
			"description": "Description here New",
			"section" : "users",
			"multiSelect": true,
			"allowText": true,
			"group": {
				"idRef": "${group2.id}", "referredObject": null
			}
		}]"""
		releasePutRequest()
		thenDataInDatabaseIs(ResourceType.class, expectedData)
		thenItemsInDatabaseIs(ResourceType.class, 1)
    }

    @Test
    public void failWhenUpdateToNoGroupReference() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:resourceTypes"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)

		// When
		String putUrl = "/resourceTypes/${userResourceTypeSingle.id}"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"id": "${userResourceTypeSingle.id}",
			"type" : "user",
			"name": "UserResourceType Single",
			"description": "Description here",
			"multiSelect": false,
			"allowText": false,
			"group": {
				"idRef": null
			}
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
    }

}
