package se.leafcoders.rosette.integration.booking

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.Booking;

public class UpdateBookingTest extends AbstractIntegrationTest {

	@Test
	public void updateBookingWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["bookings:update:${ booking2.id }", "bookings:read", "locations:read"])
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
	            "location" : { "ref" : ${ toJSON(location1) }, "text" : null }
			},
			{
				"id" : "${ booking2.id }",
				"customerName" : "Customer 2",
				"startTime" : "2012-10-26 08:30 Europe/Stockholm",
				"endTime" : "2012-10-26 11:30 Europe/Stockholm",
	            "location" : { "ref" : null, "text" : "Aspen" }
			}
		]"""
		thenDataInDatabaseIs(Booking.class, expectedData)
		thenItemsInDatabaseIs(Booking.class, 2)
	}

	@Test
	public void failWhenUpdateBookingWithInvalidContent() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["bookings:update:${ booking2.id }", "bookings:read"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String putUrl = "/bookings/${ booking2.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"customerName" : "",
			"startTime" : "2012-10-26 08:30 Europe/Stockholm",
			"endTime" : "2012-10-01 11:30 Europe/Stockholm",
            "location" : { "ref" : null, "text" : null }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)

		String expectedData = """[
			{ "property" : "customerName", "message" : "booking.customerName.notEmpty" },
			{ "property" : "location",     "message" : "booking.location.oneMustBeSet" },
			{ "property" : "",             "message" : "booking.startBeforeEndTime" }
		]"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Booking.class, 2)
	}
}
