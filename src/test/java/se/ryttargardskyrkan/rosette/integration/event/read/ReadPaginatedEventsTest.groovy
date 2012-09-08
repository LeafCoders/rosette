package se.ryttargardskyrkan.rosette.integration.event.read

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.EventTestUtil
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

public class ReadPaginatedEventsTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String events = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-04-25 11:00") + """,
			"endTime" : null
		},
		{
			"id" : "3",
			"title" : "Gudstjänst 3",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-05-25 11:00") + """,
			"endTime" : null
		},
		{
			"id" : "4",
			"title" : "Gudstjänst 4",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-06-25 11:00") + """,
			"endTime" : null
		},
		{
			"id" : "5",
			"title" : "Gudstjänst 5",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-07-25 11:00") + """,
			"endTime" : null
		},
		{
			"id" : "6",
			"title" : "Gudstjänst 6",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-08-25 11:00") + """,
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/events?page=2&per_page=2&since=" + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00"))
		getRequest.addHeader("accept", "application/json")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String exptectedEvents = """
		[{
			"id" : "3",
			"title" : "Gudstjänst 3",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-05-25 11:00") + """,
			"endTime" : null
		},
		{
			"id" : "4",
			"title" : "Gudstjänst 4",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-06-25 11:00") + """,
			"endTime" : null
		}]
		"""
		EventTestUtil.assertEventListResponseBodyIsCorrect(exptectedEvents, response)
		
		
	}
}