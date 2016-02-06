package se.leafcoders.rosette.controller.publicdata;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.calendar.Calendar;
import se.leafcoders.rosette.model.calendar.CalendarDay;
import se.leafcoders.rosette.model.calendar.CalendarEvent;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.service.PublicEventService;

@Controller
public class CalendarController extends PublicDataController {
	@Autowired
	private PublicEventService publicEventService;

	private static final String WEEK = "week";
	private static final String MONTH = "month";

	@RequestMapping(value = "calendar", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Calendar getCalendar(
			@RequestParam(defaultValue = WEEK) String rangeMode, 
			@RequestParam(defaultValue = "0") Integer rangeOffset, 
			@RequestParam(defaultValue = "1") Integer numRanges,
			@RequestParam(defaultValue = "false") boolean startFromToday, 
			HttpServletResponse response) {
		checkPermission();

		if (!rangeMode.equals(WEEK) && !rangeMode.equals(MONTH)) {
			throw new HttpMessageNotReadableException("'rangeMode' is '" + rangeMode + "' but must be specified as 'week' or 'month'.");
		}
		if (numRanges < 1 || numRanges > 6) {
			throw new HttpMessageNotReadableException("'numRanges' is '" + numRanges + "' but is only allowed to be [1,6].");
		}
		
		DateTime now = DateTime.now().withTime(0, 0, 0, 0);
		if (rangeOffset != null) {
			now = rangeMode.equals(WEEK) ? now.plusWeeks(rangeOffset) : now.plusMonths(rangeOffset); 
		}
		DateTime from, before;
		if (rangeMode.equals(WEEK)) {
			from = startFromToday ? now : now.withDayOfWeek(DateTimeConstants.MONDAY);
			before = from.plusWeeks(numRanges);
		} else {
			from = startFromToday ? now : now.withDayOfMonth(1);
			before = from.plusMonths(numRanges);
		}

		List<Event> events = publicEventService.calendarEventsBetween(from.toDate(), before.toDate());

		Calendar calendar = new Calendar();
		calendar.setYear(from.getWeekyear());
		if (rangeMode.equals(WEEK)) {
			calendar.setWeek(from.getWeekOfWeekyear());
		}
		calendar.setFromDate(from.toDate());
		calendar.setUntilDate(before.minusDays(1).toDate());

		Iterator<Event> iterEvent = events.iterator();
		Event currentEvent = iterEvent.hasNext() ? iterEvent.next() : null; 
		DateTime iterDate = from;
		while (iterDate.isBefore(before)) {
			Date endOfDay = iterDate.plusDays(1).withTime(0, 0, 0, 0).toDate();
			CalendarDay day = new CalendarDay();
			day.setDate(iterDate.toDate());
			day.setWeekDay(iterDate.getDayOfWeek());

			while (currentEvent != null && currentEvent.getStartTime().before(endOfDay)) {
				if (currentEvent.getIsPublic()) {
					CalendarEvent event = new CalendarEvent();
					event.setTitle(currentEvent.getTitle());
					event.setDescription(currentEvent.expandedDescription());
					event.setStartTime(currentEvent.getStartTime());
					event.setEndTime(currentEvent.getEndTime());
					day.addEvent(event);
				}
				currentEvent = iterEvent.hasNext() ? iterEvent.next() : null;
			}

			calendar.addDay(day);
			iterDate = iterDate.plusDays(1);
		}
		return calendar;
	}
}
