package se.ryttargardskyrkan.rosette.integration;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

public class EventTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
//		// Given
//
//		HttpClient httpClient = new DefaultHttpClient();	
//
//		// When
//		HttpGet getRequest = new HttpGet("http://localhost:8000/events");
//		getRequest.addHeader("accept", "application/json");
//		HttpResponse response = httpClient.execute(getRequest);
//		
//		// Then
//		assertEquals(200, response.getStatusLine().getStatusCode());
//		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue());
//		
//		ObjectMapper mapper = new ObjectMapper();
//		
//		// Expected
//		String expected = FileUtils.readFileToString(new ClassPathResource("eventsResponse.json").getFile(), "utf-8");
//		List<Event> expectedEvents = mapper.readValue(expected, new TypeReference<ArrayList<Event>>(){});
//		String expectedEventsAsString = mapper.writeValueAsString(expectedEvents);
//		
//		// Actual
//		String actual = IOUtils.toString(response.getEntity().getContent(), "utf-8");
//		List<Event> actualEvents = mapper.readValue(actual, new TypeReference<ArrayList<Event>>(){});
//		String actualEventsAsString = mapper.writeValueAsString(actualEvents);
//		
//		assertEquals(expectedEventsAsString, actualEventsAsString);
//
//		httpClient.getConnectionManager().shutdown();
	}
}
