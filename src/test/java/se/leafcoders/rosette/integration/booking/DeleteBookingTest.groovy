package se.leafcoders.rosette.integration.booking

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.model.Booking;

public class DeleteBookingTest extends AbstractIntegrationTest {

	@Test
	public void deleteOneBookingWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)
		givenPermissionForUser(user1, ["delete:bookings:${ booking2.id }"])

		// When
		String deleteUrl = "/bookings/${ booking2.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Booking.class, 1)
	}

	@Test
	public void deleteAllBookingsWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:bookings"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String deleteUrl = "/bookings"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Booking.class, 0)
	}

	@Test
	public void failWhenDeleteBookingWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:bookings:4711"])
		givenLocation(location1)
		givenBooking(booking1)

		// When
		String deleteUrl = "/bookings/${ booking1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(Booking.class, 1)
	}
}
