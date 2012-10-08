package se.ryttargardskyrkan.rosette.integration.eventweek.read

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.converter.RosetteDateConverter;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneConverter
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

public class ReadEventweekTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		DateTime now = DateTime.now()
		int year = now.getWeekyear()
		int week = now.getWeekOfWeekyear()
		String id = "" + year + "-W" + (week < 10 ? "0" : "") + week
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-'W'ww")
		DateTime monday = fmt.parseDateTime(id)
		monday = monday.withTime(11, 0, 0, 0)
		String previousSundayAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.minusDays(1).toDate(), "Europe/Stockholm")
		String mondayEarlyAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.toDate(), "Europe/Stockholm")
		String mondayLateAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.withTime(17, 0, 0, 0).toDate(), "Europe/Stockholm")
		String tuesdayAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.plusDays(1).toDate(), "Europe/Stockholm")
		String wednesdayAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.plusDays(2).toDate(), "Europe/Stockholm")
		String saturdayAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.plusDays(5).toDate(), "Europe/Stockholm")
		String sundayAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.plusDays(6).toDate(), "Europe/Stockholm")
		String nextMondayAsString = RosetteDateTimeTimezoneConverter.dateToString(monday.plusDays(7).toDate(), "Europe/Stockholm")
		
		String events = """
		[{
			"id" : "1",
			"title" : "Förra söndagens gudstjänst",
			"startTime" : "${previousSundayAsString}",
			"endTime" : null
		},
		{
			"id" : "2",
			"title" : "Måndagsgudstjänst 1",
			"startTime" : "${mondayEarlyAsString}",
			"endTime" : null
		},
		{
			"id" : "3",
			"title" : "Måndagsgudstjänst 2",
			"startTime" : "${mondayLateAsString}",
			"endTime" : null
		},
		{
			"id" : "4",
			"title" : "Tisdagsgudstjänst",
			"startTime" : "${tuesdayAsString}",
			"endTime" : null
		},
		{
			"id" : "5",
			"title" : "Onsdagsgudstjänst",
			"startTime" : "${wednesdayAsString}",
			"endTime" : null
		},
		{
			"id" : "6",
			"title" : "Lördagsgudstjänst",
			"startTime" : "${saturdayAsString}",
			"endTime" : null
		},
		{
			"id" : "7",
			"title" : "Söndagsgudstjänst",
			"startTime" : "${sundayAsString}",
			"endTime" : null
		},
		{
			"id" : "8",
			"title" : "Nästa måndagsgudstjänst",
			"startTime" : "${nextMondayAsString}",
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/eventweek")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		
		String expectedEventweekAsJson = """
		{
		    "week": ${week},
		    "since": "${RosetteDateConverter.dateToString(monday.toDate())}",
		    "until": "${RosetteDateConverter.dateToString(monday.plusDays(6).toDate())}",
		    "days": [{
		        "date": "${RosetteDateConverter.dateToString(monday.toDate())}",
		        "dayNumber": 1,
		        "events": [{
		            "id": "2",
		            "title": "Måndagsgudstjänst 1",
		            "startTime": "${mondayEarlyAsString}",
		            "endTime": null,
					"description" : null,
		            "themeId": null
		        }, {
		            "id": "3",
		            "title": "Måndagsgudstjänst 2",
		            "startTime": "${mondayLateAsString}",
		            "endTime": null,
					"description" : null,
		            "themeId": null
		        }]
		    }, {
		        "date": "${RosetteDateConverter.dateToString(monday.plusDays(1).toDate())}",
		        "dayNumber": 2,
		        "events": [{
		            "id": "4",
		            "title": "Tisdagsgudstjänst",
		            "startTime": "${tuesdayAsString}",
		            "endTime": null,
					"description" : null,
		            "themeId": null
		        }]
		    }, {
		        "date": "${RosetteDateConverter.dateToString(monday.plusDays(2).toDate())}",
		        "dayNumber": 3,
		        "events": [{
		            "id": "5",
		            "title": "Onsdagsgudstjänst",
		            "startTime": "${wednesdayAsString}",
		            "endTime": null,
					"description" : null,
		            "themeId": null
		        }]
		    }, {
		        "date": "${RosetteDateConverter.dateToString(monday.plusDays(3).toDate())}",
		        "dayNumber": 4,
		        "events": []
		    }, {
		        "date": "${RosetteDateConverter.dateToString(monday.plusDays(4).toDate())}",
		        "dayNumber": 5,
		        "events": []
		    }, {
		        "date": "${RosetteDateConverter.dateToString(monday.plusDays(5).toDate())}",
		        "dayNumber": 6,
		        "events": [{
		            "id": "6",
		            "title": "Lördagsgudstjänst",
		            "startTime": "${saturdayAsString}",
		            "endTime": null,
					"description" : null,
		            "themeId": null
		        }]
		    }, {
		        "date": "${RosetteDateConverter.dateToString(monday.plusDays(6).toDate())}",
		        "dayNumber": 7,
		        "events": [{
		            "id": "7",
		            "title": "Söndagsgudstjänst",
		            "startTime": "${sundayAsString}",
		            "endTime": null,
					"description" : null,
		            "themeId": null
		        }]
		    }]
		}
		"""	
		TestUtil.assertJsonResponseEquals(expectedEventweekAsJson, response)
	}
}
