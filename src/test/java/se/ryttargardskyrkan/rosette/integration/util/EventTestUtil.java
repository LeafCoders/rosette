package se.ryttargardskyrkan.rosette.integration.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import se.ryttargardskyrkan.rosette.model.Event;

public class EventTestUtil {
	public static List<Event> eventResponseToEventList(HttpResponse response) throws IllegalStateException, IOException {
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8");
		return jsonToEventList(json);
	}
	
	public static Event eventResponseToEvent(HttpResponse response) throws IllegalStateException, IOException {
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8");
		return jsonToEvent(json);
	}
	
	public static List<Event> jsonToEventList(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, new TypeReference<ArrayList<Event>>() {});
	}
	
	public static Event jsonToEvent(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, new TypeReference<Event>() {});
	}
	
	public static String eventToJson(Event event) throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(event);
	}
	
	public static String eventListToJson(List<Event> eventList) throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(eventList);
	}

	public static void assertEventListResponseBodyIsCorrect(String expectedEvents, HttpResponse response) throws IllegalStateException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		// Expected
		List<Event> expectedEventsAsList = mapper.readValue(expectedEvents, new TypeReference<ArrayList<Event>>() {});
		String expectedEventsAsString = mapper.writeValueAsString(expectedEventsAsList);

		// Actual
		List<Event> actualEventsAsList = eventResponseToEventList(response);
//		for (Event event : actualEventsAsList) {
//			event.setId(null);
//		}
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
	
	public static void assertEventIsCorrect(String expectedEvent, Event actualEvent) throws IllegalStateException, IOException {
		String expectedEventAsString = eventToJson(jsonToEvent(expectedEvent));
		String actualEventAsString = eventToJson(actualEvent);
		assertEquals(expectedEventAsString, actualEventAsString);
	}
	
	public static void assertEventWithNoIdIsCorrect(String expectedEvent, Event actualEvent) throws IllegalStateException, IOException {
		String expectedEventAsString = eventToJson(jsonToEvent(expectedEvent));
		actualEvent.setId(null);
		String actualEventAsString = eventToJson(actualEvent);
		assertEquals(expectedEventAsString, actualEventAsString);
	}
	
	public static void assertEventListIsCorrect(String expectedEvents, List<Event> actualEventList) throws IllegalStateException, IOException {
		String expectedEventsAsString = eventListToJson(jsonToEventList(expectedEvents));
		String actualEventsAsString = eventListToJson(actualEventList);
		assertEquals(expectedEventsAsString, actualEventsAsString);
	}
}
