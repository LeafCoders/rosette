package se.ryttargardskyrkan.rosette.integration.user.create

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
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
		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
		assertEquals(0L, mongoTemplate.count(new Query(), User.class))
	}
}
