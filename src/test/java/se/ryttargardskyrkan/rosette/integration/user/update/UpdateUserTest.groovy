package se.ryttargardskyrkan.rosette.integration.user.update

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
import org.springframework.data.mongodb.core.query.Order
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.model.User
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class UpdateUserTest extends AbstractIntegrationTest {

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
			"userFullName" : "Lars Arvidsson",
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/users/1")
		String requestBody = """
		{
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : "newPassword"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		
		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		
		// Asserting users in database
		Query query = new Query();
		query.sort().on("startTime", Order.ASCENDING);
		List<User> usersInDatabase = mongoTemplate.find(query, User.class);
		
		assertEquals(1L, mongoTemplate.count(new Query(), User.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : null,
			"status" : "active"
		}]""", new ObjectMapper().writeValueAsString(usersInDatabase))
		String updatedHashedPassword = mongoTemplate.getCollection("users").findOne().get("hashedPassword");
		assertTrue(updatedHashedPassword.startsWith("\$shiro1\$SHA-256\$"));
		
		// Asserting permissions in database
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"everyone" : null,
			"userId" : "1",
			"userFullName" : "Nisse Hult",
			"groupId" : null,
			"groupName" : null,
			"patterns" : ["*"]
		}]""", new ObjectMapper().writeValueAsString(mongoTemplate.findAll(Permission.class)));
	}
}
