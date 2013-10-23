package se.ryttargardskyrkan.rosette.integration.booking.read

import java.text.SimpleDateFormat
import java.util.Calendar;
import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import javax.servlet.http.HttpServletResponse
import static junit.framework.Assert.assertEquals

public class ReadActiveBookingsTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		final Calendar now = Calendar.getInstance();
		final String today = new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());

		// Given
		mongoTemplate.getCollection("bookings").insert(JSON.parse("""
		[{
			"_id" : "1",
			"customerName" : "Not active",
			"startTime" : ${TestUtil.mongoDate(today + " 00:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate(today + " 00:02 Europe/Stockholm")},
            "location" : { "idRef" : "1", "text" : null }
		},
		{
			"_id" : "2",
			"customerName" : "Active",
			"startTime" : ${TestUtil.mongoDate(today + " 00:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate(today + " 23:59 Europe/Stockholm")},
            "location" : { "idRef" : null, "text" : "Oasen" }
		},
		{
			"_id" : "3",
			"customerName" : "Active today",
			"startTime" : ${TestUtil.mongoDate(today + " 23:55 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate(today + " 23:56 Europe/Stockholm")},
            "location" : { "idRef" : null, "text" : "Aspen" }
		},
		{
			"_id" : "4",
			"customerName" : "Active today early",
			"startTime" : ${TestUtil.mongoDate(today + " 23:51 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate(today + " 23:52 Europe/Stockholm")},
            "location" : { "idRef" : null, "text" : "Boken" }
		}]
		"""))
		
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["read:bookings"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/bookings?onlyActiveToday=true")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())

        String expectedBookings = """
        [{
			"id" : "2",
			"customerName" : "Active",
			"startTime" : "${today} 00:00 Europe/Stockholm",
			"endTime" : "${today } 23:59 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : "Oasen" },
			"locationData" : null
		},
		{
			"id" : "4",
			"customerName" : "Active today early",
			"startTime" : "${today } 23:51 Europe/Stockholm",
			"endTime" : "${today } 23:52 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : "Boken" },
			"locationData" : null
        },
		{
			"id" : "3",
			"customerName" : "Active today",
			"startTime" : "${today } 23:55 Europe/Stockholm",
			"endTime" : "${today } 23:56 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : "Aspen" },
			"locationData" : null
        }]
        """
		TestUtil.assertJsonResponseEquals(expectedBookings, response)
	}
}
