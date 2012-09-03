package se.ryttargardskyrkan.rosette.integration.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import se.ryttargardskyrkan.rosette.model.Event;

public class EventTestUtil {
	public static List<Event> eventResponseToEventList(HttpResponse response) throws IllegalStateException, IOException {
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8");
		return eventsAsJsonToEventList(json);
	}
	
	public static Event eventResponseToEvent(HttpResponse response) throws IllegalStateException, IOException {
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8");
		return eventsAsJsonToEvent(json);
	}
	
	public static List<Event> eventsAsJsonToEventList(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, new TypeReference<ArrayList<Event>>() {});
	}
	
	public static Event eventsAsJsonToEvent(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, new TypeReference<Event>() {});
	}

	public static void assertEventListResponseBodyIsCorrect(String expectedEvents, HttpResponse response) throws IllegalStateException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		// Expected
		List<Event> expectedEventsAsList = mapper.readValue(expectedEvents, new TypeReference<ArrayList<Event>>() {});
		String expectedEventsAsString = mapper.writeValueAsString(expectedEventsAsList);

		// Actual
		List<Event> actualEventsAsList = eventResponseToEventList(response);
		for (Event event : actualEventsAsList) {
			event.setId(null);
		}
		String actualEventsAsString = mapper.writeValueAsString(actualEventsAsList);

		assertEquals(expectedEventsAsString, actualEventsAsString);
	}
	
	public static void assertEventResponseBodyIsCorrect(String expectedEvent, HttpResponse response) throws IllegalStateException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		// Expected
		Event expectedEventAsEvent = mapper.readValue(expectedEvent, new TypeReference<Event>() {});
		String expectedEventsAsString = mapper.writeValueAsString(expectedEventAsEvent);

		// Actual
		Event actualEvent = eventResponseToEvent(response);
		actualEvent.setId(null);
		String actualEventsAsString = mapper.writeValueAsString(actualEvent);

		assertEquals(expectedEventsAsString, actualEventsAsString);
	}
}
