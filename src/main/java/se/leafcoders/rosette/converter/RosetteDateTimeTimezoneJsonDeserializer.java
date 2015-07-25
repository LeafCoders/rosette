package se.leafcoders.rosette.converter;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/*
 * Deserializes Rosette time (string format) to java.util.Date object 
 */
public class RosetteDateTimeTimezoneJsonDeserializer extends JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
		return RosetteDateTimeTimezoneConverter.stringToDate(jsonparser.getText());
	}

}