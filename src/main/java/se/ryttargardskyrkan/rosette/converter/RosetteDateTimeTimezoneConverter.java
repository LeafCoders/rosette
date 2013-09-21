package se.ryttargardskyrkan.rosette.converter;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class RosetteDateTimeTimezoneConverter {

	private static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm";

	public static Date stringToDate(String dateAsString) {
		String[] dateParts = dateAsString.split(" ");
		String datePart = dateParts[0];
		String timePart = dateParts[1];
		String timeZonePart = dateParts[2];
		String dateTimePart = datePart + " " + timePart;

		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATETIMEFORMAT);
		formatter = formatter.withZone(DateTimeZone.forID(timeZonePart));
		DateTime dateTime = DateTime.parse(dateTimePart, formatter);
		return dateTime.toDate();
	}

	public static String dateToString(Date date, String timeZoneId) {
		DateTime dateTime = new DateTime(date.getTime(), DateTimeZone.forID(timeZoneId));
		String dateAsString = dateTime.toString(DATETIMEFORMAT) + " " + timeZoneId;

		return dateAsString;
	}
}
