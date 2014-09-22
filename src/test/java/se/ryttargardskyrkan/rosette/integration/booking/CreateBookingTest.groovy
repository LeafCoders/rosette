package se.ryttargardskyrkan.rosette.integration.booking

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.Booking;

public class CreateBookingTest extends AbstractIntegrationTest {

    @Test
    public void createBookingWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:bookings"])
		givenLocation(location1)
		
        // When
		String postUrl = "/bookings"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"customerName" : "Customer 1",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-25 15:00 Europe/Stockholm",
            "location" : { "idRef" : "${ location1.id }" }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"customerName" : "Customer 1",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-25 15:00 Europe/Stockholm",
            "location" : { "idRef" : "${ location1.id }", "text" : null, "referredObject" : null }
		}"""

		thenResponseDataIs(responseBody, expectedData)
		thenDataInDatabaseIs(Booking.class, "[${expectedData}]")
		thenItemsInDatabaseIs(Booking.class, 1)
    }

    @Test
    public void failWhenCreateBookingWithInvalidLocation() throws ClientProtocolException, IOException {
		givenUser(user1)
		givenPermissionForUser(user1, ["create:bookings"])
		givenLocation(location1)
		
        // When
		String postUrl = "/bookings"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"customerName" : "Customer 1",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-25 15:00 Europe/Stockholm",
            "location" : { "idRef" : "${ location1.id }", "text" : "Aspen" }
		}""")

        // Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
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
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """[
			{ "property" : "customerName", "message" : "booking.customerName.notEmpty" },
			{ "property" : "location",     "message" : "booking.location.oneMustBeSet" },
			{ "property" : "",             "message" : "booking.startBeforeEndTime" }
		]"""

		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Booking.class, 0)
    }
}
