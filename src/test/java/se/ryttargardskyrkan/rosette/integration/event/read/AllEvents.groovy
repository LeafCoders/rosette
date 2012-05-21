package se.ryttargardskyrkan.rosette.integration.event.read;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;


import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.TestUtil
import se.ryttargardskyrkan.rosette.model.Event;

public class AllEvents extends AbstractIntegrationTest {
	
	@Test
	public void testGetAllEvents() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.dropCollection("events")
		String events = """
		[{
			"title" : "Gudstjänst 1",
			"startTime" : null,
			"endTime" : null
		},
		{
			"title" : "Gudstjänst 2",
			"startTime" : null,
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(TestUtil.eventsAsJsonToEventList(events), "events")

		// When
		HttpGet getRequest = new HttpGet("http://localhost:9000/api/v1-snapshot/events")
		getRequest.addHeader("accept", "application/json")
		HttpResponse response = httpClient.execute(getRequest)
		
		// Then
		assertEquals(200, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertEventListResponseBodyIsCorrect(events, response)
	}
}
