package se.ryttargardskyrkan.rosette.integration.booking.read

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadBookingTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("bookings").insert(JSON.parse("""
		[{
			"_id" : "1",
			"customerName" : "Customer 1",
			"startTime" : ${TestUtil.mongoDate("2012-10-25 13:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-10-25 15:00 Europe/Stockholm")},
            "location" : { "id" : "1", "text" : null }
		},
		{
			"_id" : "2",
			"customerName" : "Customer 1",
			"startTime" : ${TestUtil.mongoDate("2012-10-26 08:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-10-26 12:00 Europe/Stockholm")},
            "location" : { "idRef" : null, "text" : "Oasen" }
		}]
        """))
		
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["read:bookings:2"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/bookings/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedBooking = """
		{
			"id" : "2",
			"customerName" : "Customer 1",
			"startTime" : "2012-10-26 08:00 Europe/Stockholm",
			"endTime" : "2012-10-26 12:00 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : "Oasen", "referredObject" : null }
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedBooking, response)
	}
}
