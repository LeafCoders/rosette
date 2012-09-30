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

import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneConverter;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event
import groovy.time.TimeCategory

public class ReadUpcomingEventsSinceTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		use (TimeCategory) {
			Date oneWeekAgo = new Date() - 1.week
			
			Calendar calendar = Calendar.getInstance()
			calendar.set(Calendar.HOUR_OF_DAY, 0)
			calendar.set(Calendar.MINUTE, 0)
			calendar.set(Calendar.SECOND, 0)
			calendar.set(Calendar.MILLISECOND, 0)
			Date todayAtMidnight = calendar.getTime()
			
			Date twoWeeksAhead = new Date() + 2.week
			
			String events = """
			[{
				"id" : "1",
				"title" : "Gudstjänst 1",
				"startTime" : "${RosetteDateTimeTimezoneConverter.dateToString(oneWeekAgo, "Europe/Stockholm")}",
				"endTime" : null
			},
			{
				"id" : "2",
				"title" : "Gudstjänst 2",
				"startTime" : "${RosetteDateTimeTimezoneConverter.dateToString(todayAtMidnight, "Europe/Stockholm")}",
				"endTime" : null
			},
			{
				"id" : "3",
				"title" : "Gudstjänst 3",
				"startTime" : "${RosetteDateTimeTimezoneConverter.dateToString(twoWeeksAhead, "Europe/Stockholm")}",
				"endTime" : null
			}]
			"""
			mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

			// When
			HttpGet getRequest = new HttpGet(baseUrl + "/events")
			getRequest.setHeader("Accept", "application/json; charset=UTF-8")
			getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
			HttpResponse response = httpClient.execute(getRequest)

			// Then
			assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
			assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
			String expectedEvents = """
			[{
				"id" : "2",
				"title" : "Gudstjänst 2",
				"startTime" : "${RosetteDateTimeTimezoneConverter.dateToString(todayAtMidnight, "Europe/Stockholm")}",
				"endTime" : null,
				"themeId" : null
			},
			{
				"id" : "3",
				"title" : "Gudstjänst 3",
				"startTime" : "${RosetteDateTimeTimezoneConverter.dateToString(twoWeeksAhead, "Europe/Stockholm")}",
				"endTime" : null,
				"themeId" : null
			}]
			"""
			TestUtil.assertJsonResponseEquals(expectedEvents, response)
		}
	}
}
