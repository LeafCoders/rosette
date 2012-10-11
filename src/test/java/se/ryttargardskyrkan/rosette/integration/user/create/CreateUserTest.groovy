package se.ryttargardskyrkan.rosette.integration.user.create

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Group
import se.ryttargardskyrkan.rosette.model.User

import com.mongodb.BasicDBList;
import com.mongodb.DBObject
import com.mongodb.util.JSON

public class CreateUserTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String groups = """
		[{
			"_id" : "1",
			"name" : "admin",
			"permissions" : ["users:create"]
		}]
		"""
		mongoTemplate.getDb().getCollection("groups").insert(JSON.parse(groups));
		
		PasswordService passwordService = new DefaultPasswordService();
		String hashedPassword = passwordService.encryptPassword("password");
		String users = """
		[{
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active",
			"groupMemberships" : [{"groupId" : "1"}]
		}]
		"""
		mongoTemplate.getDb().getCollection("users").insert(JSON.parse(users));

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
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		
		String responseJson = TestUtil.responseBodyAsString(response)
		User responseUser = new ObjectMapper().readValue(responseJson, User.class)
		
		String expectedUserInResponse = """
		{
			"id" : "${responseUser.getId()}",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : null,
			"status" : "active",
			"groupMemberships" : null
		}
		"""
		TestUtil.assertJsonEquals(expectedUserInResponse, responseJson)
		
		assertEquals(2L, mongoTemplate.count(new Query(), User.class))
		User userInDatabase = mongoTemplate.findById(responseUser.getId(), User.class);
		String expectedUserInDatabase = """
		{
			"id" : "${responseUser.getId()}",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : null,
			"status" : "active",
			"groupMemberships" : null
		}
		"""
		TestUtil.assertJsonEquals(expectedUserInDatabase, new ObjectMapper().writeValueAsString(userInDatabase))
		assertTrue(userInDatabase.hashedPassword.startsWith("\$shiro1\$SHA-256\$"))
	}
}
