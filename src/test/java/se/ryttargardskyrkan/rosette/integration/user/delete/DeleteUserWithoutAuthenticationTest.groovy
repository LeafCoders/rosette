package se.ryttargardskyrkan.rosette.integration.user.delete;

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.User

import com.mongodb.util.JSON

public class DeleteUserWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com"
		},{
			"_id" : "2",
			"username" : "nissehult"
		}]
		"""));

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/users/1")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
		assertEquals(2L, mongoTemplate.count(new Query(), User.class))
	}
}
