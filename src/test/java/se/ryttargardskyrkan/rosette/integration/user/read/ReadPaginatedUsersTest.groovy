package se.ryttargardskyrkan.rosette.integration.user.read

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.User

public class ReadPaginatedUsersTest extends AbstractIntegrationTest {

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
		},
		{
			"id" : "3",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		},
		{
			"id" : "4",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		},
		{
			"id" : "5",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		},
		{
			"id" : "6",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(users, new TypeReference<ArrayList<User>>() {}), "users")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/users?page=2&per_page=2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String exptectedUsers = """
		[{
			"id" : "3",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"hashedPassword" : null,
			"status" : null,
			"groupMemberships" : null
		},
		{
			"id" : "4",
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"hashedPassword" : null,
			"status" : null,
			"groupMemberships" : null
		}]
		"""
		TestUtil.assertJsonResponseEquals(exptectedUsers, response)
		
		StringBuilder sb = new StringBuilder()
		sb.append("<users?page=1&per_page=2>; rel=\"previous\"")
		sb.append(",")
		sb.append("<users?page=3&per_page=2>; rel=\"next\"")
		Header linkHeader = response.getFirstHeader("Link")
		assertEquals(sb.toString(), linkHeader.getValue())
	}
}
