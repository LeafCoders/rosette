package se.ryttargardskyrkan.rosette.integration.group.update

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Order
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Group
import se.ryttargardskyrkan.rosette.model.User

import com.mongodb.util.JSON

public class UpdateGroupTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new DefaultPasswordService().encryptPassword("password");
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

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/groups/2")
		String requestBody = """
		{
			"name" : "Translators",
			"description" : "Super translators",			
			"permissions" : ["*"]
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
			"description" : null,
			"permissions" : ["*"]
		},{
			"id" : "2",
			"name" : "Translators",
			"description" : "Super translators",
			"permissions" : ["*"]
		}]
		""", new ObjectMapper().writeValueAsString(groupsInDatabase))
	}
}
