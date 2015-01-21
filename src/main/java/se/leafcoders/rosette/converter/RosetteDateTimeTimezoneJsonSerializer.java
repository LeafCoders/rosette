package se.leafcoders.rosette.converter;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/*
* Serializes java.util.Date object to Rosette time (string format) 
*/
public class RosetteDateTimeTimezoneJsonSerializer extends JsonSerializer<Date> {
	
	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		String json = RosetteDateTimeTimezoneConverter.dateToString(value, "Europe/Stockholm");
		jgen.writeString(json);
	}

}