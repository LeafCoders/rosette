package se.ryttargardskyrkan.rosette.integration.permission.create

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Permission
import se.ryttargardskyrkan.rosette.model.Permission
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class CreatePermissionTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new RosettePasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"firstName" : "Lars",
			"lastName" : "Arvidsson",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));
		 
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		}]
		"""));
	
		mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins",
		}]
		"""));

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/permissions")
		String requestBody = """
		{
			"groupId" : "1",
			"patterns" : ["events:*"]
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		
		// Asserting response
		String responseJson = TestUtil.jsonFromResponse(response)
		Permission responsePermission = new ObjectMapper().readValue(responseJson, Permission.class)
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responsePermission.getId()}",
			"everyone" : null,
			"userId" : null,
			"userFullName" : null,
			"groupId" : "1",
			"groupName" : "Admins",
			"patterns" : ["events:*"]
		}
		""", responseJson)
		
		// Asserting database
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
			"id" : "${responsePermission.getId()}",
			"everyone" : null,
			"userId" : null,
			"userFullName" : null,
			"groupId" : "1",
			"groupName" : "Admins",
			"patterns" : ["events:*"]
		}]
		""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(Permission.class)))
	}
}
