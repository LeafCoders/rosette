package se.leafcoders.rosette.integration.group

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.Group

public class UpdateGroupTest extends AbstractIntegrationTest {

	@Test
	public void updateGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:groups:${ group1.id }", "read:groups"])
		givenGroup(group1)
		givenGroup(group2)

		// When
		String putUrl = "/groups/${ group1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"name" : "Staff",
			"description" : "Doing stuff"
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[
			{
				"id" : "${ group2.id }",
				"name" : "Users",
				"description" : null
			},
			{
				"id" : "${ group1.id }",
				"name" : "Staff",
				"description" : "Doing stuff"
			}
		]"""
		thenDataInDatabaseIs(Group.class, expectedData)
		thenItemsInDatabaseIs(Group.class, 2)
	}

	@Test
	public void failUpdateGroupThatDontExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:groups", "read:groups"])
		givenGroup(group1)
		givenGroup(group2)

		// When
		String putUrl = "/groups/4711"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"name" : "Staff",
			"description" : "Doing stuff"
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_NOT_FOUND)
		thenItemsInDatabaseIs(Group.class, 2)
	}
}
