package se.leafcoders.rosette.core.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class RosetteDateTimeJsonDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 8889762207250422354L;

    public RosetteDateTimeJsonDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TimeZone home = TimeZone.getTimeZone("Europe/Stockholm");

        LocalDateTime clientDateTime = LocalDateTime.parse(parser.readValueAs(String.class));
        ZonedDateTime timezoneDateTime = ZonedDateTime.of(clientDateTime, home.toZoneId());
        ZonedDateTime utcDateTime = timezoneDateTime.withZoneSameInstant(ZoneOffset.UTC);
        return utcDateTime.toLocalDateTime();
    }
}
