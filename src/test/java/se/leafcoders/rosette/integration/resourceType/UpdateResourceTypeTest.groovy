package se.leafcoders.rosette.integration.resourceType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UserResourceType

public class UpdateResourceTypeTest extends AbstractIntegrationTest {

    @Test
    public void updateUserResourceTypeWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroup(group2)
		givenResourceType(userResourceTypeSingle)
		givenPermissionForUser(user1, ["update:resourceTypes:${userResourceTypeSingle.id}", "read:resourceTypes", "read:groups"])

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
			"group": ${ toJSON(group2) }
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
			"group": ${ toJSON(group2) }
		}]"""
		releasePutRequest()
		thenDataInDatabaseIs(ResourceType.class, expectedData)
		thenItemsInDatabaseIs(ResourceType.class, 1)
    }
}
