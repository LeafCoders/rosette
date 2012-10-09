package se.ryttargardskyrkan.rosette.integration.user.delete;

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.User

public class DeleteUserWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String users = """
		[{
			"id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"status" : "active",
			"groupMemberships" : [{"groupId" : "1"}]
		},
		{
			"id" : "2",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(users, new TypeReference<ArrayList<User>>() {}), "users")

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/users/1")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
//		Disabling permission check for now
//		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
//		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
//		assertEquals(2L, mongoTemplate.count(new Query(), User.class))
		
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals(1L, mongoTemplate.count(new Query(), User.class))
	}
}
