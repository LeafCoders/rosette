package se.ryttargardskyrkan.rosette.integration.booking

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.Booking;

public class UpdateBookingTest extends AbstractIntegrationTest {

	@Test
	public void updateBookingWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:bookings:${ booking2.id }"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String putUrl = "/bookings/${ booking2.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"customerName" : "Customer 2",
			"startTime" : "2012-10-26 08:30 Europe/Stockholm",
			"endTime" : "2012-10-26 11:30 Europe/Stockholm",
            "location" : { "text" : "Aspen" }
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[
			{
				"id" : "${ booking1.id }",
				"customerName" : "Scan",
				"startTime" : "2012-03-25 11:00 Europe/Stockholm",
				"endTime" : "2012-03-26 11:00 Europe/Stockholm",
	            "location" : { "idRef" : "${ location1.id }", "text" : null, "referredObject" : null }
			},
			{
				"id" : "${ booking2.id }",
				"customerName" : "Customer 2",
				"startTime" : "2012-10-26 08:30 Europe/Stockholm",
				"endTime" : "2012-10-26 11:30 Europe/Stockholm",
	            "location" : { "idRef" : null, "text" : "Aspen", "referredObject" : null }
			}
		]"""
		thenDataInDatabaseIs(Booking.class, expectedData)
		thenItemsInDatabaseIs(Booking.class, 2)
	}

	@Test
	public void failWhenUpdateBookingWithInvalidContent() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:bookings:${ booking2.id }"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String putUrl = "/bookings/${ booking2.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"customerName" : "",
			"startTime" : "2012-10-26 08:30 Europe/Stockholm",
			"endTime" : "2012-10-01 11:30 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : null }
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)

		String responseBody = TestUtil.jsonFromResponse(putResponse)
		String expectedData = """[
			{ "property" : "customerName", "message" : "booking.customerName.notEmpty" },
			{ "property" : "location",     "message" : "booking.location.oneMustBeSet" },
			{ "property" : "",             "message" : "booking.startBeforeEndTime" }
		]"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Booking.class, 2)
	}
}
