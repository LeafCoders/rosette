package se.leafcoders.rosette.persistence.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RosetteDateTimeJsonSerializer extends StdSerializer<LocalDateTime> {

    private static final long serialVersionUID = -772157233527001088L;

    public RosetteDateTimeJsonSerializer() {
        super(LocalDateTime.class);
    }

    public void serialize(LocalDateTime utcDateTime, JsonGenerator generator, SerializerProvider provider) throws IOException {
        LocalDateTime defaultDateTime = fromUtcToDefaultTimeZone(utcDateTime);
        generator.writeString(defaultDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    public static LocalDateTime fromUtcToDefaultTimeZone(LocalDateTime utcTime) {
        ZonedDateTime utcDateTime = utcTime.atZone(ZoneOffset.UTC);
        ZonedDateTime timezoneDateTime = utcDateTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
        LocalDateTime defaultDateTime = timezoneDateTime.toLocalDateTime();
        return defaultDateTime;
    }
    
    public static Date defaultTimeZoneAsDate(LocalDateTime defaultDateTime) {
        return Date.from(defaultDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
