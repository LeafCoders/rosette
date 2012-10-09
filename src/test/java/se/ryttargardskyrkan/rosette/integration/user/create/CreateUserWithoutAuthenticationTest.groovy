package se.ryttargardskyrkan.rosette.integration.user.create

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.User

public class CreateUserWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		
		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/users")
		String requestBody = """
		{
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		HttpResponse response = httpClient.execute(postRequest)

		// Then
//		Disabling permission check for now
//		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
//		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
//		assertEquals(0L, mongoTemplate.count(new Query(), User.class))
		
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		
		String responseJson = TestUtil.responseBodyAsString(response)
		User responseUser = new ObjectMapper().readValue(responseJson, User.class)
		
		String expectedUser = """
		{
			"id" : "${responseUser.getId()}",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"hashedPassword" : null,
			"status" : null,
			"groupMemberships" : null
		}
		"""
		TestUtil.assertJsonEquals(expectedUser, responseJson)
		
		assertEquals(1L, mongoTemplate.count(new Query(), User.class))
		User userInDatabase = mongoTemplate.findById(responseUser.getId(), User.class);
		TestUtil.assertJsonEquals(expectedUser, new ObjectMapper().writeValueAsString(userInDatabase))
	}
}
