package se.ryttargardskyrkan.rosette.integration.group.update

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
		},{
			"_id" : "2",
			"name" : "Translators",
			"permissions" : ["translatorPermission"]
		}]
		"""));
		
		mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"groupId" : "1"
		}]
		"""));
	
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		},{
			"_id" : "2",
			"groupId" : "2",
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/groups/2")
		String requestBody = """
		{
			"name" : "Translators",
			"description" : "Super translators"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		
		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		
		// Asserting groups in database
		Query queryGroups = new Query();
		List<Group> groupsInDatabase = mongoTemplate.find(queryGroups, Group.class);
		
		assertEquals(2L, mongoTemplate.count(new Query(), Group.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"name" : "Admins",
			"description" : null
		},{
			"id" : "2",
			"name" : "Translators",
			"description" : "Super translators"
		}]
		""", new ObjectMapper().writeValueAsString(groupsInDatabase))
		
		
		// Asserting permissions in database
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"everyone" : null,
			"userId" : "1",
			"userFullName" : null,
			"groupId" : null,
			"groupName" : null,
			"patterns" : ["*"]
		},{
			"id" : "2",
			"everyone" : null,
			"userId" : null,
			"userFullName" : null,
			"groupId" : "2",
			"groupName" : "Translators",
			"patterns" : ["*"]
		}]""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(Permission.class)));

	}
}
