package se.ryttargardskyrkan.rosette.integration.eventweek.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

import com.mongodb.util.JSON

public class ReadEventWeekWithSpecificWeekTest extends AbstractIntegrationTest {

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
		
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/eventWeeks/2012-W39")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		
		String expectedEventWeekAsJson = """
		{
		    "week": 39,
		    "since": "2012-09-24",
		    "until": "2012-09-30",
		    "days": [{
		        "date": "2012-09-24",
		        "dayNumber": 1,
		
		        "events": [{
		            "id": "2",
		            "title": "Måndagsgudstjänst 1",
		            "startTime": "2012-09-24 11:00 Europe/Stockholm",
		            "endTime": null,
					"description" : null,
					"eventType":null,
			        "location":null,
					"requiredUserResourceTypes" : null,
					"userResources" : null
		        }, {
		            "id": "3",
		            "title": "Måndagsgudstjänst 2",
		            "startTime": "2012-09-24 17:00 Europe/Stockholm",
		            "endTime": null,
					"description" : null,
					"eventType":null,
			        "location":null,
					"requiredUserResourceTypes" : null,
					"userResources" : null
		        }]
		    }, {
		        "date": "2012-09-25",
		        "dayNumber": 2,
		        "events": [{
		            "id": "4",
		            "title": "Tisdagsgudstjänst",
		            "startTime": "2012-09-25 11:00 Europe/Stockholm",
		            "endTime": null,
					"description" : null,
					"eventType":null,
			        "location":null,
					"requiredUserResourceTypes" : null,
					"userResources" : null
		        }]
		    }, {
		        "date": "2012-09-26",
		        "dayNumber": 3,
		        "events": [{
		            "id": "5",
		            "title": "Onsdagsgudstjänst",
		            "startTime": "2012-09-26 11:00 Europe/Stockholm",
		            "endTime": null,
					"description" : null,
					"eventType":null,
			        "location":null,
					"requiredUserResourceTypes" : null,
					"userResources" : null
		        }]
		    }, {
		        "date": "2012-09-27",
		        "dayNumber": 4,
		        "events": []
		    }, {
		        "date": "2012-09-28",
		        "dayNumber": 5,
		        "events": []
		    }, {
		        "date": "2012-09-29",
		        "dayNumber": 6,
		        "events": [{
		            "id": "6",
		            "title": "Lördagsgudstjänst",
		            "startTime": "2012-09-29 11:00 Europe/Stockholm",
		            "endTime": null,
					"description" : null,
					"eventType":null,
			        "location":null,
					"requiredUserResourceTypes":null,
					"userResources":null
		        }]
		    }, {
		        "date": "2012-09-30",
		        "dayNumber": 7,
		        "events": [{
		            "id": "7",
		            "title": "Söndagsgudstjänst",
		            "startTime": "2012-09-30 11:00 Europe/Stockholm",
		            "endTime": null,
					"description" : null,
					"eventType":null,
			        "location":null,
					"requiredUserResourceTypes":null,
					"userResources":null
		        }]
		    }]
		}
		}
		"""	
		TestUtil.assertJsonResponseEquals(expectedEventWeekAsJson, response)
	}
}
