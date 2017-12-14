package se.leafcoders.rosette.persistence.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RosetteDateTimeJsonSerializer extends StdSerializer<LocalDateTime> {

    private static final long serialVersionUID = -772157233527001088L;

    public RosetteDateTimeJsonSerializer() {
        super(LocalDateTime.class);
    }

    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
