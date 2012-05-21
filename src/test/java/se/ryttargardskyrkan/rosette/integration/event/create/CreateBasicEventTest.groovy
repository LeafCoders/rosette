package se.ryttargardskyrkan.rosette.integration.event.create

import static org.junit.Assert.*

import java.util.List

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate


import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

public class CreateBasicEventTest extends AbstractIntegrationTest {
	
	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.dropCollection("events")	

		// When
		HttpPost postRequest = new HttpPost("http://localhost:9000/api/v1-snapshot/events")
		String requestBody = """
		{
			"title" : "Gudstjänst",
			"startTime" : "1337508000000"
		}
		"""
		StringEntity stringEntity = new StringEntity(requestBody, "UTF-8")
		stringEntity.setContentType("application/json;charset=UTF-8")
		postRequest.setEntity(stringEntity)
		HttpResponse response = httpClient.execute(postRequest)
		
		// Then		
		assertEquals(201, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		
		String expectedEvents = """
		{
			"title" : "Gudstjänst",
			"startTime" : 1337508000000,
			"endTime" : null
		}
		"""
		TestUtil.assertEventResponseBodyIsCorrect(expectedEvents, response);
		
//		List<Event> expectedEventsAsList = mapper.readValue(expectedEvents, new TypeReference<ArrayList<Event>>(){})
//		List<Event> eventsInDatabase = mongoTemplate.findAll(Event.class)
//		eventsInDatabase[0].id = null
//		assertEquals(mapper.writeValueAsString(expectedEventsAsList), mapper.writeValueAsString(eventsInDatabase))
	}
}
