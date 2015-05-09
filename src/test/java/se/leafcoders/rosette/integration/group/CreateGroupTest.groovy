package se.leafcoders.rosette.integration.group

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.Group
import com.mongodb.util.JSON

public class CreateGroupTest extends AbstractIntegrationTest {

	@Test
	public void createGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["groups:create"])
		
        // When
		String postUrl = "/groups"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "theSpecialTeam",
			"name" : "Translators",
			"description" : "Translators from swedish to english."
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "theSpecialTeam",
			"name" : "Translators",
			"description" : "Translators from swedish to english."
		}"""

		thenResponseDataIs(responseBody, expectedData)
		thenDataInDatabaseIs(Group.class, "[${expectedData}]")
		thenItemsInDatabaseIs(Group.class, 1)
	}

	@Test
	public void failCreateGroupWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["groups:read"])

        // When
		String postUrl = "/groups"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "theSpecialTeam",
			"name" : "Translators",
			"description" : "Translators from swedish to english."
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(Group.class, 0)
	}

	@Test
	public void failCreateGroupWhenMissingId() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["groups:create"])

        // When
		String postUrl = "/groups"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"name" : "Translators"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{ "property" : "id", "message" : "error.id.mustBeUnique" }
		]"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Group.class, 0)
	}
}
