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

public class PublicCalendarTest extends AbstractIntegrationTest {

	@Test
	public void readWeekCalendarWithPublicEvents() throws ClientProtocolException, IOException {
		// Given
		DateTime now = new DateTime(new Date());
		event1.startTime = now.dayOfWeek().setCopy(3).toDate();
		event1.endTime = now.dayOfWeek().setCopy(4).toDate();
		event1.isPublic = true
		event2.startTime = now.dayOfWeek().setCopy(5).toDate();
		event2.endTime = now.dayOfWeek().setCopy(6).toDate();
		event2.isPublic = true
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
		String getUrl = "/public/calendar"
		HttpResponse getResponse = whenGet(getUrl)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"year": ${ now.getYear() },
			"week": ${ now.getWeekOfWeekyear() },
			"fromDate": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(1).toDate()) }",
			"untilDate": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(7).toDate()) }",
			"days": [
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(1).toDate()) }",
					"weekDay":1,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(2).toDate()) }",
					"weekDay": 2,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(3).toDate()) }",
					"weekDay": 3,
					"events": [
						{
							"title": "${ event1.title }",
							"description": "Event description.\\nSingleUser: User One",
							"startTime": "${ TestUtil.dateToModelTime(event1.startTime) }",
							"endTime": "${ TestUtil.dateToModelTime(event1.endTime) }"
						}
					]
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(4).toDate()) }",
					"weekDay": 4,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(5).toDate()) }",
					"weekDay": 5,
					"events": [
						{
							"title": "${ event2.title }",
							"description": null,
							"startTime": "${ TestUtil.dateToModelTime(event2.startTime) }",
							"endTime": "${ TestUtil.dateToModelTime(event2.endTime) }"
						}
					]
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(6).toDate()) }",
					"weekDay": 6,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(7).toDate()) }",
					"weekDay": 7,
					"events": []
				}
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void readWeekCalendarWithPrivateEvents() throws ClientProtocolException, IOException {
		// Given
		DateTime now = new DateTime(new Date());
		event1.startTime = now.dayOfWeek().setCopy(3).toDate();
		event1.endTime = now.dayOfWeek().setCopy(4).toDate();
		event1.isPublic = false
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
		String getUrl = "/public/calendar"
		HttpResponse getResponse = whenGet(getUrl)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"year": ${ now.getYear() },
			"week": ${ now.getWeekOfWeekyear() },
			"fromDate": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(1).toDate()) }",
			"untilDate": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(7).toDate()) }",
			"days": [
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(1).toDate()) }",
					"weekDay":1,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(2).toDate()) }",
					"weekDay": 2,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(3).toDate()) }",
					"weekDay": 3,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(4).toDate()) }",
					"weekDay": 4,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(5).toDate()) }",
					"weekDay": 5,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(6).toDate()) }",
					"weekDay": 6,
					"events": []
				},
				{
					"date": "${ TestUtil.dateToModelDate(now.dayOfWeek().setCopy(7).toDate()) }",
					"weekDay": 7,
					"events": []
				}
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

}
