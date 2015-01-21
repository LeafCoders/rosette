package se.leafcoders.rosette.converter;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class RosetteDateConverter {

	private static final String DATEFORMAT = "yyyy-MM-dd";

	public static Date stringToDate(String dateAsString) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATEFORMAT);
		DateTime dateTime = DateTime.parse(dateAsString, formatter);
		return dateTime.toDate();
	}

	public static String dateToString(Date date) {
		DateTime dateTime = new DateTime(date);
		String dateAsString = dateTime.toString(DATEFORMAT);
		return dateAsString;
	}
}
