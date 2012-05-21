package se.ryttargardskyrkan.rosette.integration.event.read

import static org.junit.Assert.*

import java.io.IOException
import java.util.List

import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate


import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

public class AllEventsSinceTest extends AbstractIntegrationTest {
	
	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.dropCollection("events")
		String eventsInDatabase = """
		[{
			"title" : "Gudstjänst 1",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null
		},
		{
			"title" : "Gudstjänst 2",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-04-25 11:00") + """,
			"endTime" : null
		},
		{
			"title" : "Gudstjänst 3",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-05-25 11:00") + """,
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(TestUtil.eventsAsJsonToEventList(eventsInDatabase), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/events?since=" + TestUtil.dateTimeAsUnixTime("2012-04-25 11:00"))
		getRequest.addHeader("accept", "application/json")
		HttpResponse response = httpClient.execute(getRequest)
		
		// Then		
		assertEquals(200, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedEvents = """
		[{
			"title" : "Gudstjänst 2",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-04-25 11:00") + """,
			"endTime" : null
		},
		{
			"title" : "Gudstjänst 3",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-05-25 11:00") + """,
			"endTime" : null
		}]
		"""
		TestUtil.assertEventListResponseBodyIsCorrect(expectedEvents, response)
	}
}
