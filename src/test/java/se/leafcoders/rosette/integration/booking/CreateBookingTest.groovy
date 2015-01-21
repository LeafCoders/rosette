package se.leafcoders.rosette.integration.booking

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.Booking;

public class CreateBookingTest extends AbstractIntegrationTest {

    @Test
    public void createBookingWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:bookings", "read:locations"])
		givenLocation(location1)
		
        // When
		String postUrl = "/bookings"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"customerName" : "Customer 1",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-25 15:00 Europe/Stockholm",
            "location" : { "ref" : { "id" : "${ location1.id }" } }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"customerName" : "Customer 1",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-25 15:00 Europe/Stockholm",
            "location" : { "ref" : ${ toJSON(location1) }, "text" : null }
		}"""

		thenResponseDataIs(responseBody, expectedData)
		thenDataInDatabaseIs(Booking.class, "[${expectedData}]")
		thenItemsInDatabaseIs(Booking.class, 1)
    }

    @Test
    public void failWhenCreateBookingWithInvalidLocation() throws ClientProtocolException, IOException {
		givenUser(user1)
		givenPermissionForUser(user1, ["create:bookings", "read:locations"])
		givenLocation(location1)
		
        // When
		String postUrl = "/bookings"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"customerName" : "Customer 1",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-25 15:00 Europe/Stockholm",
            "location" : { "ref" : { "id" : "${ location1.id }" }, "text" : "Aspen" }
		}""")

        // Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{ "property" : "location", "message" : "booking.location.oneMustBeSet" }
		]"""

		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Booking.class, 0)
    }

    @Test
    public void failWhenCreateBookingWithInvalidContent() throws ClientProtocolException, IOException {
		givenUser(user1)
		givenPermissionForUser(user1, ["create:bookings"])
		givenLocation(location1)
		
        // When
		String postUrl = "/bookings"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"customerName" : "",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-19 15:00 Europe/Stockholm",
            "location" : null
		}""")

        // Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{ "property" : "customerName", "message" : "booking.customerName.notEmpty" },
			{ "property" : "location",     "message" : "booking.location.oneMustBeSet" },
			{ "property" : "",             "message" : "booking.startBeforeEndTime" }
		]"""

		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Booking.class, 0)
    }
}
