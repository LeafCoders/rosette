package se.leafcoders.rosette.integration.publicdata

import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.DateTime;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class PublicBookingsTest extends AbstractIntegrationTest {

	@Test
	public void readAllActiveBookings() throws ClientProtocolException, IOException {
		// Given
		booking1.startTime = new DateTime(new Date()).plusMinutes(5).toDate();
		booking1.endTime = new DateTime(new Date()).plusMinutes(10).toDate();
		booking2.startTime = new DateTime(new Date()).minusMinutes(15).toDate();
		booking2.endTime = new DateTime(new Date()).minusMinutes(5).toDate();
		givenPermissionForEveryone(["public:read"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String getUrl = "/public/bookings"
		HttpResponse getResponse = whenGet(getUrl)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ booking1.id }",
				"customerName" : "Scan",
				"startTime" : "${ TestUtil.dateToModelTime(booking1.startTime) }",
				"endTime" : "${ TestUtil.dateToModelTime(booking1.endTime) }",
				"location" : { "ref" : ${ toJSON(location1) }, "text" : null }
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
