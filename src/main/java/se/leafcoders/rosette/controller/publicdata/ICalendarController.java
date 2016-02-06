package se.leafcoders.rosette.controller.publicdata;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.service.PublicEventService;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TzUrlDotOrgGenerator;
import biweekly.io.text.ICalWriter;
import biweekly.property.DateEnd;
import biweekly.property.DateStart;
import biweekly.property.Description;
import biweekly.property.Summary;
import biweekly.util.Duration;

@Controller
public class ICalendarController extends PublicDataController {
	@Autowired
	private PublicEventService publicEventService;

	@SuppressWarnings("resource")
	@RequestMapping(value = "icalendar", method = RequestMethod.GET, produces = "text/calendar")
	@ResponseBody
	public String getICalendar(
			@RequestParam(required = true) String[] eventType,
			HttpServletResponse response) {
		checkPermission();

		List<Event> events = getEvents(eventType);

		ICalendar ical = new ICalendar();
		Iterator<Event> iterEvent = events.iterator();
		while (iterEvent.hasNext()) {
			ical.addEvent(createVEvent(iterEvent.next()));
		}

		StringWriter writer = new StringWriter();
		ICalWriter iCalWriter = new ICalWriter(writer, ICalVersion.V2_0);
		iCalWriter.getTimezoneInfo().setGenerator(new TzUrlDotOrgGenerator(true));
		iCalWriter.getTimezoneInfo().setDefaultTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
		try {
			iCalWriter.write(ical);
		} catch (IOException ignore) {
		}
		return writer.toString();
	}

	private List<Event> getEvents(String[] eventTypes) {
		DateTime eventsFrom = DateTime.now().minusDays(15);
		DateTime eventsBefore = DateTime.now().plusYears(1);
		return publicEventService.readManyForEventTypes(eventsFrom.toDate(), eventsBefore.toDate(), eventTypes);
	}

	private VEvent createVEvent(Event event) {
		VEvent vEvent = new VEvent();

		String id = event.getId();
		vEvent.setUid("efefefef-" + id.substring(0,4) + "-" + id.substring(4,8) + "-" + id.substring(8,12) + "-" + id.substring(12,24));

		Summary summary = vEvent.setSummary(event.getTitle());
		summary.setLanguage("sv-SE");

		if (event.getDescription() != null) {
			Description description = vEvent.setDescription(event.expandedDescription());
			description.setLanguage("sv-SE");
		}

		vEvent.setSequence(event.getVersion());

		vEvent.setDateStart(new DateStart(event.getStartTime()));
		if (event.getEndTime() != null) {
			vEvent.setDateEnd(new DateEnd(event.getEndTime()));
		} else {
			vEvent.setDuration(Duration.fromMillis(60*60*1000));
		}

		if (event.getLocation() != null) {
			if (event.getLocation().hasRef()) {
				vEvent.setLocation(event.getLocation().getRef().getName());
			} else {
				vEvent.setLocation(event.getLocation().getText());
			}
		}

		return vEvent;
	}
}
