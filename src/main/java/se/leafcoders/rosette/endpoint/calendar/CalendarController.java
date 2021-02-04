package se.leafcoders.rosette.endpoint.calendar;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TimezoneAssignment;
import biweekly.io.text.ICalWriter;
import biweekly.property.DateEnd;
import biweekly.property.DateStart;
import biweekly.property.Description;
import biweekly.property.Summary;
import biweekly.util.Duration;
import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.endpoint.event.Event;
import se.leafcoders.rosette.endpoint.event.EventService;
import se.leafcoders.rosette.util.ClientServerTime;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/calendar", produces = "application/json")
public class CalendarController {

    private final EventService eventService;

    @GetMapping(value = "feed", produces = "text/calendar")
    public String getCalendarFeed(@RequestParam(required = true) List<Long> eventTypeId) {
        List<Event> events = getEvents(eventTypeId);

        ICalendar ical = new ICalendar();
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Stockholm"); // Add to setting if use outside Sweden
        TimezoneAssignment defaultTimezone = TimezoneAssignment.download(timeZone, false);
        ical.getTimezoneInfo().setDefaultTimezone(defaultTimezone);

        Iterator<Event> iterEvent = events.iterator();
        while (iterEvent.hasNext()) {
            ical.addEvent(createVEvent(iterEvent.next()));
        }

        StringWriter writer = new StringWriter();
        ICalWriter iCalWriter = new ICalWriter(writer, ICalVersion.V2_0);
        try {
            iCalWriter.write(ical);
            iCalWriter.close();
        } catch (IOException ignore) {
        }
        return writer.toString();
    }

    private List<Event> getEvents(List<Long> eventTypeIds) {
        LocalDateTime eventsFrom = ClientServerTime.serverTimeNow().minusDays(15);
        LocalDateTime eventsBefore = ClientServerTime.serverTimeNow().plusYears(1);
        return eventService.readForCalendar(eventTypeIds, eventsFrom, eventsBefore);
    }

    private VEvent createVEvent(Event event) {
        VEvent vEvent = new VEvent();

        Long id = event.getId();
        vEvent.setUid("uid-" + id.toString() + "@leafcoders.se");

        Summary summary = vEvent.setSummary(event.getTitle());
        summary.setLanguage("sv-SE");

        if (event.getDescription() != null) {
            Description description = vEvent.setDescription(event.getDescription());
            description.setLanguage("sv-SE");
        }

        vEvent.setSequence(event.getVersion());

        LocalDateTime startTimeInDefaultTimeZone = RosetteDateTimeJsonSerializer
                .fromUtcToDefaultTimeZone(event.getStartTime());
        vEvent.setDateStart(
                new DateStart(RosetteDateTimeJsonSerializer.defaultTimeZoneAsDate(startTimeInDefaultTimeZone)));
        if (event.getEndTime() != null) {
            LocalDateTime endTimeInDefaultTimeZone = RosetteDateTimeJsonSerializer
                    .fromUtcToDefaultTimeZone(event.getEndTime());
            vEvent.setDateEnd(
                    new DateEnd(RosetteDateTimeJsonSerializer.defaultTimeZoneAsDate(endTimeInDefaultTimeZone)));
        } else {
            vEvent.setDuration(Duration.fromMillis(60 * 60 * 1000));
        }
        /*
         * if (event.getLocation() != null) { if (event.getLocation().hasRef()) {
         * vEvent.setLocation(event.getLocation().getRef().getName()); } else {
         * vEvent.setLocation(event.getLocation().getText()); } }
         */
        return vEvent;
    }
}
