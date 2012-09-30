package se.ryttargardskyrkan.rosette.integration.eventweek.read

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.EventTestUtil
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event
import se.ryttargardskyrkan.rosette.model.Eventweek

public class ReadEventweekWithSpecificWeekTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String events = """
		[{
			"id" : "1",
			"title" : "Förra söndagens gudstjänst",
			"startTime" : "2012-09-23 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "2",
			"title" : "Måndagsgudstjänst 1",
			"startTime" : "2012-09-24 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "3",
			"title" : "Måndagsgudstjänst 2",
			"startTime" : "2012-09-24 17:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "4",
			"title" : "Tisdagsgudstjänst",
			"startTime" : "2012-09-25 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "5",
			"title" : "Onsdagsgudstjänst",
			"startTime" : "2012-09-26 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "6",
			"title" : "Lördagsgudstjänst",
			"startTime" : "2012-09-29 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "7",
			"title" : "Söndagsgudstjänst",
			"startTime" : "2012-09-30 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "8",
			"title" : "Nästa måndagsgudstjänst",
			"startTime" : "2012-10-01 11:00 Europe/Stockholm",
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/eventweek/2012-39")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String responseJson = IOUtils.toString(response.getEntity().getContent(), "utf-8")
		
		String expectedEventweekAsJson = """
		{
		  "week": 39,
		  "months": [9],
		  "days": {
		    "1": {
		      "date": "2012-09-24 00:00 Europe/Stockholm",
		      "events": [{
		        "id": "2",
		        "title": "Måndagsgudstjänst 1",
		        "startTime": "2012-09-24 11:00 Europe/Stockholm",
		        "endTime": null,
		        "themeId": null
		      }, {
		        "id": "3",
		        "title": "Måndagsgudstjänst 2",
		        "startTime": "2012-09-24 17:00 Europe/Stockholm",
		        "endTime": null,
		        "themeId": null
		      }]
		    },
		    "2": {
		      "date": "2012-09-25 00:00 Europe/Stockholm",
		      "events": [{
		        "id": "4",
		        "title": "Tisdagsgudstjänst",
		        "startTime": "2012-09-25 11:00 Europe/Stockholm",
		        "endTime": null,
		        "themeId": null
		      }]
		    },
		    "3": {
		      "date": "2012-09-26 00:00 Europe/Stockholm",
		      "events": [{
		        "id": "5",
		        "title": "Onsdagsgudstjänst",
		        "startTime": "2012-09-26 11:00 Europe/Stockholm",
		        "endTime": null,
		        "themeId": null
		      }]
		    },
		    "4": {
		      "date": "2012-09-27 00:00 Europe/Stockholm",
		      "events": []
		    },
		    "5": {
		      "date": "2012-09-28 00:00 Europe/Stockholm",
		      "events": []
		    },
		    "6": {
		      "date": "2012-09-29 00:00 Europe/Stockholm",
		      "events": [{
		        "id": "6",
		        "title": "Lördagsgudstjänst",
		        "startTime": "2012-09-29 11:00 Europe/Stockholm",
		        "endTime": null,
		        "themeId": null
		      }]
		    },
		    "7": {
		      "date": "2012-09-30 00:00 Europe/Stockholm",
		      "events": [{
		        "id": "7",
		        "title": "Söndagsgudstjänst",
		        "startTime": "2012-09-30 11:00 Europe/Stockholm",
		        "endTime": null,
		        "themeId": null
		      }]
		    }
		  }
		}
		"""	
		TestUtil.assertJsonEquals(expectedEventweekAsJson, expectedEventweekAsJson)
	}
}
