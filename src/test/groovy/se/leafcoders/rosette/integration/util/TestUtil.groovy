package se.leafcoders.rosette.integration.util

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import java.text.SimpleDateFormat
import java.util.Iterator
import java.util.Map;
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import se.leafcoders.rosette.converter.RosetteDateConverter
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneConverter
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

public class TestUtil {

	public static void assertJsonEquals(String expectedJson, String actualJson) throws JsonProcessingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper()
		JsonNode expectedNode = objectMapper.readTree(expectedJson)
        JsonNode actualNode = objectMapper.readTree(actualJson)
        
        //assertEquals(expectedNode, actualNode)
        Map result = assertJsonNode(expectedNode, actualNode)
        if (result) {
            assertEquals("""${result.path}, Actual: "${result.actual}", Expected: "${result.expected}". JSON""", expectedNode, actualNode)
        }
	}

    private static Map assertJsonNode(JsonNode expectedNode, JsonNode actualNode, String path = "") {
        if (expectedNode == null) {
            return null
        }
        if (expectedNode.isValueNode()) {
            if (expectedNode?.asText() != actualNode?.asText()) {
                return [path: path, expected: expectedNode?.toString(), actual: actualNode?.toString()]
            }
            return null
        }

        if (expectedNode.isArray()) {
            Iterator<JsonNode> expectedIter = expectedNode.iterator()
            Iterator<JsonNode> actualIter = actualNode.iterator()
            int i = 0
            while (expectedIter.hasNext() && actualIter.hasNext()) {
                Map result = assertJsonNode(expectedIter.next(), actualIter.next(), path + "[${ i++ }]")
                if (result) {
                    return result
                }
            }
        } else {
            Map<String, JsonNode> expectedMap = expectedNode.fields().collectEntries()// { Map.Entry<String, JsonNode> it -> return [(it.key): it.value] }
            Map<String, JsonNode> actualMap = actualNode.fields().collectEntries { Map.Entry<String, JsonNode> it -> return [(it.key): it.value] }
            
            if (expectedMap.size() != actualMap.size()) {
                return [path: path + " not equal sizes", expected: expectedMap.size(), actual: actualMap.size()]
            }
 
            Map error = null
            expectedMap.forEach { String key, JsonNode node ->
                error = error ?: assertJsonNode(node, actualMap[key], path + "->" + key)
            }
            return error
        }
        return null
    }

	public static void assertJsonResponseEquals(String expectedJson, HttpResponse response) throws JsonProcessingException, IOException {
		assertJsonEquals(expectedJson, jsonFromResponse(response))
	}
	
	public static String jsonFromResponse(HttpResponse response) throws IllegalStateException, IOException {
		return IOUtils.toString(response.getEntity().getContent(), "utf-8")
	}
	
	/*
	 * Converts Rosette time format to MongoDb format.
	 * Use this when inserting json directly into database without using a model.
	 */
	public static String mongoDate(String rosetteDateTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		GregorianCalendar calendar = new GregorianCalendar(new SimpleTimeZone(0, "GMT"))
        format.setCalendar(calendar)
        
		Date date = RosetteDateTimeTimezoneConverter.stringToDate(rosetteDateTime)
		
		return "{\$date:\"" + format.format(date) + "\"}"
	}
	
	/*
	 * Converts Rosette time format to server model format.
	 * Use this when Setting date parameter in a server model.
	 */
	public static Date modelDate(String rosetteDateTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		GregorianCalendar calendar = new GregorianCalendar(new SimpleTimeZone(0, "GMT"))
        format.setCalendar(calendar)
		return RosetteDateTimeTimezoneConverter.stringToDate(rosetteDateTime)
	}

	/*
	 * Converts java Date to server model DateTime format.
	 */
	public static String dateToModelTime(Date javaDate) {
		return RosetteDateTimeTimezoneConverter.dateToString(javaDate, "Europe/Stockholm")
	}

	/*
	 * Converts java Date to server model Date format.
	 */
	public static String dateToModelDate(Date javaDate) {
		return RosetteDateConverter.dateToString(javaDate)
	}
}
