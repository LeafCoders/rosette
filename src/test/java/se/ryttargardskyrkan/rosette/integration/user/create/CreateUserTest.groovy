package se.ryttargardskyrkan.rosette.integration.user.create

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
import se.ryttargardskyrkan.rosette.model.User
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class CreateUserTest extends AbstractIntegrationTest {

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
		}]
		"""));

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/users")
		String requestBody = """
		{
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : "password"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)
		
		// Then

		// Asserting response
		String responseJson = TestUtil.jsonFromResponse(response)
		User responseUser = new ObjectMapper().readValue(responseJson, User.class)
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseUser.getId()}",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : null,
			"status" : "active"
		}
		""", responseJson)
		
		// Asserting database
		User userInDatabase = mongoTemplate.findById(responseUser.getId(), User.class)
		int numberOfUsersInDatabase = mongoTemplate.count(new Query(), User.class)
		
		assertEquals(2L, numberOfUsersInDatabase)
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseUser.getId()}",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : null,
			"status" : "active"
		}
		""", new ObjectMapper().writeValueAsString(userInDatabase))
		assertTrue(userInDatabase.hashedPassword.startsWith("\$shiro1\$SHA-256\$"))
	}
}
