package se.leafcoders.rosette.matcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.persistence.converter.ClientServerTime;
import se.leafcoders.rosette.persistence.model.Persistable;

public class Matchers {

    public static org.hamcrest.Matcher<Map<String, String>> isValidationError(String property, String message) {
        Map<String, String> map = new TreeMap<>();
        map.put("property", property);
        map.put("message", message);
        return org.hamcrest.core.Is.<Map<String, String>>is(map);
    }

    public static org.hamcrest.Matcher<Integer> isIdOf(Persistable persistable) {
        return org.hamcrest.core.Is.<Integer>is(persistable.getId().intValue());
    }

    public static org.hamcrest.Matcher<String> isDateTime(LocalDateTime serverDateTime) {
        LocalDateTime clientDateTime = ClientServerTime.serverToClient(serverDateTime);
        return org.hamcrest.core.Is.<String>is(clientDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public static org.hamcrest.Matcher<String> isApiError(ApiError error) {
        return org.hamcrest.core.Is.<String>is(error.toString());
    }
}
