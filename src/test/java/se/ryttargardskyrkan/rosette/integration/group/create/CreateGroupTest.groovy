package se.ryttargardskyrkan.rosette.integration.group.create

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Group
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class CreateGroupTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new RosettePasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));
		 
		mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins",
			"permissions" : ["*"]
		}]
		"""));
		
		mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"groupId" : "1"
		}]
		"""));

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/groups")
		String requestBody = """
		{
			"name" : "Translators",
			"description" : "Translators from swedish to english."
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
				
		// Asserting response
		String responseJson = TestUtil.jsonFromResponse(response)
		Group responseGroup = new ObjectMapper().readValue(responseJson, new TypeReference<Group>() {})
		
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseGroup.getId()}",
			"name" : "Translators",
			"description" : "Translators from swedish to english.",
			"permissions" : ["groups:update:${responseGroup.getId()}"]
		}
		""", responseJson)
		
		// Asserting database
		List<Group> groupsInDatabase = mongoTemplate.findAll(Group.class)
		
		assertEquals(2L, mongoTemplate.count(new Query(), Group.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"name" : "Admins",
			"description":null,
			"permissions" : ["*"]
		},{
			"id" : "${responseGroup.getId()}",
			"name" : "Translators",
			"description" : "Translators from swedish to english.",
			"permissions" : ["groups:update:${responseGroup.getId()}"]
		}]
		""", new ObjectMapper().writeValueAsString(groupsInDatabase))
	}
}
