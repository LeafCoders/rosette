package se.ryttargardskyrkan.rosette.integration.event.update

import static org.junit.Assert.*

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
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Order
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.EventTestUtil
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.*

public class UpdateEventTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		PasswordService passwordService = new DefaultPasswordService();
		String hashedPassword = passwordService.encryptPassword("password");
		String groups = """
		[{
			"id" : "1",
			"name" : "admin",
			"permissions" : ["events:update"]
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(groups, new TypeReference<ArrayList<Group>>() {}), "groups");
		String users = """
		[{
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active",
			"groupMemberships" : [{"groupId" : "1"}]
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(users, new TypeReference<ArrayList<User>>() {}), "users")
		String events = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : "2012-04-26 11:00 Europe/Stockholm",
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/events/1")
		String requestBody = """
		{
			"id" : "1",
			"title" : "Gudstjänst 1 uppdaterad",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null
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
			"title" : "Gudstjänst 1 uppdaterad",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : "2012-04-26 11:00 Europe/Stockholm",
			"endTime" : null
		}]
		"""
		Query query = new Query();
		query.sort().on("startTime", Order.ASCENDING);
		List<Event> eventsInDatabase = mongoTemplate.find(query, Event.class);
		EventTestUtil.assertEventListIsCorrect(expectedEvents, eventsInDatabase);
	}
}
