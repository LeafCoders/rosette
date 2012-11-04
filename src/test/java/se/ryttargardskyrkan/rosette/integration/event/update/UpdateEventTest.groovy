package se.ryttargardskyrkan.rosette.integration.event.update

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
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Order
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

import com.mongodb.util.JSON

public class UpdateEventTest extends AbstractIntegrationTest {

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
		},
		{
			"_id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : ${TestUtil.mongoDate("2012-04-25 11:00 Europe/Stockholm")},
			"description" : "Dopgudstjänst"
		}]
		"""))

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/events/2")
		String requestBody = """
		{
			"id" : "1",
			"title" : "Gudstjänst 2 uppdaterad",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"description" : "Nattvard"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals(2L, mongoTemplate.count(new Query(), Event.class))
		String expectedEvents = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"description" : null,			
			"startTime" : null,
			"endTime" : null,
			"themeId" : null
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2 uppdaterad",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : "Nattvard",
			"themeId" : null
		}]
		"""
		Query query = new Query();
		query.sort().on("startTime", Order.ASCENDING);
		List<Event> eventsInDatabase = mongoTemplate.find(query, Event.class);
		TestUtil.assertJsonEquals(expectedEvents, new ObjectMapper().writeValueAsString(eventsInDatabase))
	}
}
