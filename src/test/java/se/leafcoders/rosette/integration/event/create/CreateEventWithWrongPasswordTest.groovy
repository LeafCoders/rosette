package se.leafcoders.rosette.integration.event.create

import static junit.framework.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.security.RosettePasswordService
import com.mongodb.util.JSON

public class CreateEventWithWrongPasswordTest extends AbstractIntegrationTest {

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
		HttpPost postRequest = new HttpPost(baseUrl + "/events")
		String requestBody = """
		{
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "asdf"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode())
		assertEquals("Unauthorized", response.getStatusLine().getReasonPhrase())
		assertEquals(0L, mongoTemplate.count(new Query(), Event.class))
	}
}
