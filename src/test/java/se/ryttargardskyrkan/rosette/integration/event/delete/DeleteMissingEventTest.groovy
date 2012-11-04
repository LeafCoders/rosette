package se.ryttargardskyrkan.rosette.integration.event.delete;

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.auth.BasicScheme
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.Event

import com.mongodb.util.JSON

public class DeleteMissingEventTest extends AbstractIntegrationTest {

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
		
		mongoTemplate.getCollection("events").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Gudstjänst 1"
		},{
			"_id" : "2",
			"title" : "Gudstjänst 2"
		}]
		"""));

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/events/3")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		deleteRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), deleteRequest));
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
		assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatusLine().getStatusCode())
		assertEquals("Not Found", response.getStatusLine().getReasonPhrase())
		assertEquals(2L, mongoTemplate.count(new Query(), Event.class))
	}
}
