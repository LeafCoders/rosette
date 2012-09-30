package se.ryttargardskyrkan.rosette.converter;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class RosetteDateTimeTimezoneJsonDeserializer extends JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
		return RosetteDateTimeTimezoneConverter.stringToDate(jsonparser.getText());
	}

}