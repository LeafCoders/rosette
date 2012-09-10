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
import se.ryttargardskyrkan.rosette.model.Theme

public class ReadEventsByThemeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String themes = """
		[{
			"id" : "1",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		},
		{
			"id" : "2",
			"title" : "Johannesevangeliet",
			"description" : "Vi läser igenom johannesevangeliet"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(themes, new TypeReference<ArrayList<Theme>>() {}), "themes")
		
		String events = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null,
			"themeId" : "1"
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-04-25 11:00") + """,
			"endTime" : null,
			"themeId" : "2"
		},
		{
			"id" : "3",
			"title" : "Gudstjänst 3",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-05-25 11:00") + """,
			"endTime" : null,
			"themeId" : "1"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/events?themeId=1")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedEvents = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null,
			"themeId" : "1"
		},
		{
			"id" : "3",
			"title" : "Gudstjänst 3",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-05-25 11:00") + """,
			"endTime" : null,
			"themeId" : "1"
		}]
		"""
		EventTestUtil.assertEventListResponseBodyIsCorrect(expectedEvents, response)
	}
}