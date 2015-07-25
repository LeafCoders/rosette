package se.leafcoders.rosette.converter;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

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