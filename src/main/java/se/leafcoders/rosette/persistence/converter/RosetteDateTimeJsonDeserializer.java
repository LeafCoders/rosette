package se.leafcoders.rosette.persistence.converter;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * https://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
 */
public class RosetteDateTimeJsonDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 8889762207250422354L;

    public RosetteDateTimeJsonDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return LocalDateTime.parse(parser.readValueAs(String.class));
    }
}
