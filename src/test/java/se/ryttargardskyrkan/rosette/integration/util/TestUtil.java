package se.ryttargardskyrkan.rosette.integration.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneConverter;

public class TestUtil {

	public static String responseBodyAsString(HttpResponse httpResponse) throws IOException {
		return IOUtils.toString(httpResponse.getEntity().getContent(), "utf-8");
	}
	
	public static void assertJsonEquals(String expectedJson, String actualJson) throws JsonProcessingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(actualJson));
	}
	
	public static void assertJsonResponseEquals(String expectedJson, HttpResponse response) throws JsonProcessingException, IOException {
		assertJsonEquals(expectedJson, jsonFromResponse(response));
	}
	
	public static String jsonFromResponse(HttpResponse response) throws IllegalStateException, IOException {
		return IOUtils.toString(response.getEntity().getContent(), "utf-8");
	}
	
	public static String mongoDate(String rosetteDateTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		GregorianCalendar calendar = new GregorianCalendar(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);
        
		Date date = RosetteDateTimeTimezoneConverter.stringToDate(rosetteDateTime);
		
		return "{$date:\"" + format.format(date) + "\"}";
	}
	
}
