package se.leafcoders.rosette.integration.publicdata

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet
import org.joda.time.DateTime;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.User;

public class PublicICalendarTest extends AbstractIntegrationTest {

	@Test
	public void readCalendar() throws ClientProtocolException, IOException {
		// Given
		DateTime now = new DateTime(new Date());
		event1.startTime = now.dayOfWeek().setCopy(3).toDate();
		event1.endTime = now.dayOfWeek().setCopy(4).toDate();
		event1.isPublic = true
		event2.startTime = now.dayOfWeek().setCopy(5).toDate();
		event2.endTime = now.dayOfWeek().setCopy(6).toDate();
		event2.isPublic = false
		givenPermissionForEveryone(["public:read"])
		givenLocation(location1)
		givenEventType(eventType1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeMulti)
		givenEvent(event1)
		givenEvent(event2)

		// When
		String getUrl = "/public/icalendar?eventType=${ event1.eventType.id }&eventType=${ event2.eventType.id }"
		HttpResponse getResponse = whenGetICalendar(getUrl)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "text/calendar;charset=UTF-8")

		assertTrue(responseBody.count("BEGIN:VEVENT") == 1);
		assertTrue(responseBody.count("SUMMARY;LANGUAGE=sv-SE:An event") == 1);
		assertTrue(responseBody.count("DESCRIPTION;LANGUAGE=sv-SE:Event description.\\nSingleUser: User One") == 1);
	}

	protected HttpResponse whenGetICalendar(String getUrl) {
		HttpGet getRequest = new HttpGet(baseUrl + getUrl)
		getRequest.addHeader("Accept", "text/calendar; charset=UTF-8")
		getRequest.addHeader("Content-Type", "text/calendar; charset=UTF-8")
		HttpResponse resp = httpClient.execute(getRequest)
		return resp
	}

}
