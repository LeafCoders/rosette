package se.ryttargardskyrkan.rosette.integration.user.update

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.User

import com.mongodb.util.JSON

public class UpdateMissingUserTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new DefaultPasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));
		 
		mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins",
			"permissions" : ["*"]
		}]
		"""));
		
		mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"groupId" : "1"
		}]
		"""));

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/users/3")
		String requestBody = """
		{
			"username" : "larsabrasha",
			"firstName" : "Nisse",
			"lastName" : "Hult"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatusLine().getStatusCode())
		assertEquals("Not Found", response.getStatusLine().getReasonPhrase())
		assertEquals(1L, mongoTemplate.count(new Query(), User.class))
	}
}
