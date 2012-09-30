package se.ryttargardskyrkan.rosette.integration.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TestUtil {

	public static String responseBodyAsString(HttpResponse httpResponse) throws IOException {
		return IOUtils.toString(httpResponse.getEntity().getContent(), "utf-8");
	}

	public static long dateTimeAsUnixTime(String time) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));

		return simpleDateFormat.parse(time).getTime();
	}
	
	public static void assertJsonEquals(String expectedJson, String actualJson) throws JsonProcessingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		assertEquals(objectMapper.readTree(expectedJson).toString(), objectMapper.readTree(actualJson).toString());
	}
	
	public static void assertJsonResponseEquals(String expectedJson, HttpResponse response) throws JsonProcessingException, IOException {
		assertJsonEquals(expectedJson, jsonFromResponse(response));
	}
	
	public static String jsonFromResponse(HttpResponse response) throws IllegalStateException, IOException {
		return IOUtils.toString(response.getEntity().getContent(), "utf-8");
	}
	
}
