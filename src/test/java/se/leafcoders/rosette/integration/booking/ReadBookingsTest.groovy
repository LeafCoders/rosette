package se.leafcoders.rosette.integration.booking

import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class ReadBookingsTest extends AbstractIntegrationTest {

	@Test
	public void readAllBookingsWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:bookings", "read:locations"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String getUrl = "/bookings"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ booking1.id }",
				"customerName" : "Scan",
				"startTime" : "2012-03-25 11:00 Europe/Stockholm",
				"endTime" : "2012-03-26 11:00 Europe/Stockholm",
				"location" : { "ref" : ${ toJSON(location1) }, "text" : null }
			},
			{
				"id" : "${ booking2.id }",
				"customerName" : "Arla",
				"startTime" : "2014-01-21 11:00 Europe/Stockholm",
				"endTime" : "2014-01-22 12:00 Europe/Stockholm",
				"location" : { "ref" : null, "text" : "A location" }
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
	
	@Test
	public void readActiveBookingsWithSuccess() throws ClientProtocolException, IOException {
		final Calendar now = Calendar.getInstance();
		final String today = new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());

		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:bookings"])

		mongoTemplate.getCollection("bookings").insert(JSON.parse("""[
			{
				"_id" : "1",
				"customerName" : "Not active",
				"startTime" : ${ TestUtil.mongoDate(today + " 00:00 Europe/Stockholm") },
				"endTime" : ${ TestUtil.mongoDate(today + " 00:02 Europe/Stockholm") },
	            "location" : { "ref" : { "id" : "1" }, "text" : null }
			},
			{
				"_id" : "2",
				"customerName" : "Active",
				"startTime" : ${ TestUtil.mongoDate(today + " 00:00 Europe/Stockholm") },
				"endTime" : ${ TestUtil.mongoDate(today + " 23:59 Europe/Stockholm") },
	            "location" : { "ref" : null, "text" : "Oasen" }
			},
			{
				"_id" : "3",
				"customerName" : "Active today",
				"startTime" : ${ TestUtil.mongoDate(today + " 23:55 Europe/Stockholm") },
				"endTime" : ${ TestUtil.mongoDate(today + " 23:56 Europe/Stockholm") },
	            "location" : { "ref" : null, "text" : "Aspen" }
			},
			{
				"_id" : "4",
				"customerName" : "Active today early",
				"startTime" : ${ TestUtil.mongoDate(today + " 23:51 Europe/Stockholm") },
				"endTime" : ${ TestUtil.mongoDate(today + " 23:52 Europe/Stockholm") },
	            "location" : { "ref" : null, "text" : "Boken" }
			}
		]"""))

		// When
		String getUrl = "/bookings?onlyActiveToday=true"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
	        {
				"id" : "2",
				"customerName" : "Active",
				"startTime" : "${ today } 00:00 Europe/Stockholm",
				"endTime" : "${ today } 23:59 Europe/Stockholm",
	            "location" : { "ref" : null, "text" : "Oasen" }
			},
			{
				"id" : "4",
				"customerName" : "Active today early",
				"startTime" : "${ today } 23:51 Europe/Stockholm",
				"endTime" : "${ today } 23:52 Europe/Stockholm",
	            "location" : { "ref" : null, "text" : "Boken" }
	        },
			{
				"id" : "3",
				"customerName" : "Active today",
				"startTime" : "${ today } 23:55 Europe/Stockholm",
				"endTime" : "${ today } 23:56 Europe/Stockholm",
	            "location" : { "ref" : null, "text" : "Aspen" }
	        }
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
