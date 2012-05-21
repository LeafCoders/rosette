package se.ryttargardskyrkan.rosette.integration.event.create

import static org.junit.Assert.*

import java.io.IOException
import java.text.SimpleDateFormat;
import java.util.List
import java.util.TimeZone

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate


import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.TestUtil;
import se.ryttargardskyrkan.rosette.model.Event

public class CreateFullEventTest extends AbstractIntegrationTest {
	
	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.dropCollection("events")

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/events")
		String requestBody = """
		{
			"title" : "Gudstjänst",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null
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
		[{
			"title" : "Gudstjänst",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null
		}]
		"""
		List<Event> expectedEventsAsList = mapper.readValue(expectedEvents, new TypeReference<ArrayList<Event>>(){})
		List<Event> eventsInDatabase = mongoTemplate.findAll(Event.class)
		eventsInDatabase[0].id = null
		assertEquals(mapper.writeValueAsString(expectedEventsAsList), mapper.writeValueAsString(eventsInDatabase))
	}
}
