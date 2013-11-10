package se.ryttargardskyrkan.rosette.integration.event.create

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.junit.Assert.*
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class CreateEventTest extends AbstractIntegrationTest {

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
		 
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/events")
		String requestBody = """
		{
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"description" : "Nattvard och dop"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		
		// Asserting response
		String responseJson = TestUtil.jsonFromResponse(response)
		Event responseEvent = new ObjectMapper().readValue(responseJson, new TypeReference<Event>() {})
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseEvent.getId()}",
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : "Nattvard och dop",
			"eventType":null,
			"location":null,
			"requiredUserResourceTypes":null,
			"userResources":null
		}
		""", responseJson)
		
		// Asserting database
		Event eventInDatabase = mongoTemplate.findOne(new Query(), Event.class)
		
		assertEquals(1L, mongoTemplate.count(new Query(), Event.class))
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseEvent.getId()}",
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"eventType":null,
			"location":null,
			"description" : "Nattvard och dop",
			"requiredUserResourceTypes":null,
			"userResources":null
		}
		""", new ObjectMapper().writeValueAsString(eventInDatabase))
	}
}
