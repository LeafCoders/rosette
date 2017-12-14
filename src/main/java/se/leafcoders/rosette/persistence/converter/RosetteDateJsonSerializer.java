package se.leafcoders.rosette.persistence.converter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * https://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
 */
public class RosetteDateJsonSerializer extends StdSerializer<LocalDate> {

    private static final long serialVersionUID = -7764256191758481895L;

    public RosetteDateJsonSerializer() {
        super(LocalDate.class);
    }

    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
