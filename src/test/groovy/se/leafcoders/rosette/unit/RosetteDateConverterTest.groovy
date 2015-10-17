package se.leafcoders.rosette.unit

import static org.junit.Assert.assertEquals
import java.text.ParseException
import org.junit.Test
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneConverter

public class RosetteDateConverterTest {
	
	@Test
	public void testStringToDate() throws ParseException {
		assertEquals(1353060000000L, RosetteDateTimeTimezoneConverter.stringToDate("2012-11-16 11:00 Europe/Stockholm").getTime())
		assertEquals(1353060000000L, RosetteDateTimeTimezoneConverter.stringToDate("2012-11-16 10:00 Europe/London").getTime())
	}
	
	@Test
	public void testDateToString() throws ParseException {
		Date date = new Date(1353060000000L)
		
		assertEquals("2012-11-16 11:00 Europe/Stockholm", RosetteDateTimeTimezoneConverter.dateToString(date, "Europe/Stockholm"))
		assertEquals("2012-11-16 10:00 Europe/London", RosetteDateTimeTimezoneConverter.dateToString(date, "Europe/London"))
	}
}
