package se.leafcoders.rosette.converter;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class RosetteDateJsonSerializer extends JsonSerializer<Date> {
	
	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		String json = RosetteDateConverter.dateToString(value);
		jgen.writeString(json);
	}

}