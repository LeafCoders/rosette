package se.ryttargardskyrkan.rosette.integration.event.read;

import static org.junit.Assert.*

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.EventTestUtil;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.Event

public class ReadUpcomingEventsWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.dropCollection("events")
		String events = """
		[{
			"title" : "Gudstjänst 1",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null
		},
		{
			"title" : "Gudstjänst 2",
			"startTime" : null,
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/events")
		getRequest.addHeader("accept", "application/json")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(200, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		EventTestUtil.assertEventListResponseBodyIsCorrect(events, response)
	}
}
