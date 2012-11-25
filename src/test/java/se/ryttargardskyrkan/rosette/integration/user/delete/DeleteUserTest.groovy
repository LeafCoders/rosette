package se.ryttargardskyrkan.rosette.integration.user.delete;

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.GroupMembership
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.model.User
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class DeleteUserTest extends AbstractIntegrationTest {

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
		},{
			"_id" : "2",
			"username" : "nissehult",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));

		mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins"
		}]
		"""));

		mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"groupId" : "1"
		},{
			"_id" : "2",
			"userId" : "2",
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
			"userId" : "2",
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/users/2")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		deleteRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), deleteRequest));
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
		
		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		
		// Asserting users in database
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"firstName" : null,
			"lastName" : null,
			"password" : null,
			"status" : "active"
		}]
		""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(User.class)))
		
		// Asserting groupMemberships in database	
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"userId" : "1",
			"groupId" : "1"
		}]
		""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(GroupMembership.class)))
		
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
		}]
		""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(Permission.class)))
	}
}
