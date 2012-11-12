package se.ryttargardskyrkan.rosette.integration.permission.update

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

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Permission
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class UpdatePermissionTest extends AbstractIntegrationTest {

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
		 
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		},{
			"_id" : "2",
			"everyone" : true,
			"patterns" : ["permissions:*"]
		}]
		"""));

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/permissions/2")
		String requestBody = """
		{
			"id" : "2",
			"everyone" : true,
			"patterns" : ["events:read:*", "themes:read:*"]
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"everyone" : null,
			"userId" : "1",
			"groupId" : null,
			"patterns" : ["*"]
		},{
			"id" : "2",
			"everyone" : true,
			"userId" : null,
			"groupId" : null,
			"patterns" : ["events:read:*", "themes:read:*"]
		}]
		""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(Permission.class)))
	}
}
