package se.ryttargardskyrkan.rosette.integration.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

public class TestUtil {

	public static String responseBodyAsString(HttpResponse httpResponse) throws IOException {
		return IOUtils.toString(httpResponse.getEntity().getContent(), "utf-8");
	}

	public static long dateTimeAsUnixTime(String time) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));

		return simpleDateFormat.parse(time).getTime();
	}
	
}
