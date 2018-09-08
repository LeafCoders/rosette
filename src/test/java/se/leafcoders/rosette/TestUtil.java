package se.leafcoders.rosette;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;

public class TestUtil {

    public static void assertJsonEquals(String expectedJson, String actualJson) throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedNode = objectMapper.readTree(expectedJson);
        JsonNode actualNode = objectMapper.readTree(actualJson);

        // assertEquals(expectedNode, actualNode)
        Map<String, String> result = assertJsonNode(expectedNode, actualNode, "");
        if (result != null && result.size() > 0) {
            assertEquals(
                result.get("path") + ", Actual: \"" + result.get("actual") + "\", Expected: \"" + result.get("expected") + "\". JSON", expectedNode, actualNode
            );
        }
    }

    private static Map<String, String> assertJsonNode(JsonNode expectedNode, JsonNode actualNode, String path) {
        if (expectedNode == null) {
            return null;
        }
        if (actualNode == null) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("path", path);
            result.put("expected", expectedNode != null ? expectedNode.toString() : null);
            result.put("actual", "Node does not exist");
            return result;
        }
        if (expectedNode.isNull() != actualNode.isNull()) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("path", path);
            result.put("expected", expectedNode != null ? expectedNode.toString() : null);
            result.put("actual", actualNode != null ? actualNode.toString() : null);
            return result;
        }
        if (expectedNode.isValueNode()) {
            if (expectedNode != null && actualNode != null && expectedNode.asText() != actualNode.asText()) {
                Map<String, String> result = new HashMap<String, String>();
                result.put("path", path);
                result.put("expected", expectedNode != null ? expectedNode.toString() : null);
                result.put("actual", actualNode != null ? actualNode.toString() : null);
                return result;
            }
            return null;
        }

        if (expectedNode.isArray()) {
            if (expectedNode.size() != actualNode.size()) {
                Map<String, String> result = new HashMap<String, String>();
                result.put("path", path);
                result.put("expected", expectedNode != null ? expectedNode.toString() : null);
                result.put("actual", actualNode != null ? actualNode.toString() : null);
                return result;
            }
            Iterator<JsonNode> expectedIter = expectedNode.iterator();
            Iterator<JsonNode> actualIter = actualNode.iterator();
            int i = 0;
            while (expectedIter.hasNext() && actualIter.hasNext()) {
                Map<String, String> result = assertJsonNode(expectedIter.next(), actualIter.next(), path + "[" + (i++) + "]");
                if (result != null && result.size() > 0) {
                    return result;
                }
            }
        } else {
            Map<String, JsonNode> expectedMap = Streams.stream(expectedNode.fields()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
            Map<String, JsonNode> actualMap = Streams.stream(actualNode.fields()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

            if (expectedMap.size() != actualMap.size()) {
                Map<String, String> result = new HashMap<String, String>();
                result.put("path", path + " not equal sizes");
                result.put("expected", "" + expectedMap.size());
                result.put("actual", "" + actualMap.size());
                return result;
            }

            Optional<Map<String, String>> error = expectedMap.entrySet().stream()
                .map(e -> assertJsonNode(e.getValue(), actualMap.get(e.getKey()), path + "->" + e.getKey())).filter(e -> e != null).findFirst();
            return error.isPresent() ? error.get() : null;
        }
        return null;
    }
    /*
     * public static void assertJsonResponseEquals(String expectedJson,
     * HttpResponse response) throws JsonProcessingException, IOException {
     * assertJsonEquals(expectedJson, jsonFromResponse(response)); }
     * 
     * public static String jsonFromResponse(HttpResponse response) throws
     * IllegalStateException, IOException { return
     * IOUtils.toString(response.getEntity().getContent(), "utf-8"); }
     */
    /*
     * Converts Rosette time format to MongoDb format. Use this when inserting
     * json directly into database without using a model.
     */
    /*
     * public static String mongoDate(String rosetteDateTime) { SimpleDateFormat
     * format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
     * GregorianCalendar calendar = new GregorianCalendar(new SimpleTimeZone(0,
     * "GMT")); format.setCalendar(calendar);
     * 
     * Date date =
     * RosetteDateTimeTimezoneConverter.stringToDate(rosetteDateTime);
     * 
     * return "{\$date:\"" + format.format(date) + "\"}"; }
     */

    /*
     * Converts Rosette time format to server model format. Use this when
     * Setting date parameter in a server model.
     */
    /*
     * public static Date modelDate(String rosetteDateTime) { SimpleDateFormat
     * format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
     * GregorianCalendar calendar = new GregorianCalendar(new SimpleTimeZone(0,
     * "GMT")); format.setCalendar(calendar); return
     * RosetteDateTimeTimezoneConverter.stringToDate(rosetteDateTime); }
     */

    /*
     * Converts java Date to server model DateTime format.
     */
    /*
     * public static String dateToModelTime(Date javaDate) { return
     * RosetteDateTimeTimezoneConverter.dateToString(javaDate,
     * "Europe/Stockholm"); }
     */

    /*
     * Converts java Date to server model Date format.
     */
    /*
     * public static String dateToModelDate(Date javaDate) { return
     * RosetteDateConverter.dateToString(javaDate); }
     */
}
