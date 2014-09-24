package se.ryttargardskyrkan.rosette.integration.group

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Group
import se.ryttargardskyrkan.rosette.model.Permission
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class UpdateGroupTest extends AbstractIntegrationTest {

	@Test
	public void updateGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:groups:${ group1.id }"])
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
		givenPermissionForUser(user1, ["update:groups"])
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
