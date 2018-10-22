package se.leafcoders.rosette.persistence.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RosetteDateTimeJsonSerializer extends StdSerializer<LocalDateTime> {

    private static final long serialVersionUID = -772157233527001088L;

    public RosetteDateTimeJsonSerializer() {
        super(LocalDateTime.class);
    }

    public void serialize(LocalDateTime time, JsonGenerator generator, SerializerProvider provider) throws IOException {
        ZonedDateTime utcDateTime = time.atZone(ZoneOffset.UTC);
        ZonedDateTime timezoneDateTime = utcDateTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
        LocalDateTime clientDateTime = timezoneDateTime.toLocalDateTime();
        generator.writeString(clientDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
