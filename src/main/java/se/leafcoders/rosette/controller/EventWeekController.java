package se.leafcoders.rosette.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.event.EventDay;
import se.leafcoders.rosette.model.event.EventWeek;
import se.leafcoders.rosette.service.SecurityService;

@Controller
public class EventWeekController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SecurityService security;
	
	@RequestMapping(value = "eventWeeks/current", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EventWeek getCurrentEventWeek(HttpServletResponse response) {
		security.checkPermission("read:eventweek");
		DateTime now = DateTime.now();
		int year = now.getWeekyear();
		int week = now.getWeekOfWeekyear();
		String id = "" + year + "-W" + (week < 10 ? "0" : "") + week;
		
		return this.getEventWeek(id, response);
	}

	@RequestMapping(value = "eventWeeks/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EventWeek getEventWeek(@PathVariable String id, HttpServletResponse response) {
		security.checkPermission("read:eventweek");
		int weekyear = Integer.parseInt(id.substring(0, 4));
		int weekOfWeekyear = Integer.parseInt(id.substring(6, 8));
		
		DateTime since = new DateTime();
		since = since.withWeekyear(weekyear);
		since = since.withWeekOfWeekyear(weekOfWeekyear);
		since = since.withTimeAtStartOfDay();
		since = since.withDayOfWeek(DateTimeConstants.MONDAY);
		
		EventWeek eventWeek = new EventWeek();
		eventWeek.setWeek(since.weekOfWeekyear().get());
		eventWeek.setSince(since.toDate());
		eventWeek.setUntil(since.plusDays(6).toDate());
		
		List <EventDay> days = new ArrayList<EventDay>();
		
		for (int i = 1; i <= 7; i++) {
			EventDay eventday = new EventDay();
			
			DateTime sinceDay = since.plusDays(i - 1);
			DateTime untilDay = since.plusDays(i);
			
			Query query = new Query();
			query.with(new Sort(Sort.Direction.ASC, "startTime"));
			Criteria criteria = new Criteria();
			criteria = Criteria.where("startTime").gte(sinceDay.toDate());
			criteria = criteria.lt(untilDay.toDate());
			query.addCriteria(criteria);
			
			List<Event> eventsInDatabase = mongoTemplate.find(query, Event.class);
			List<Event> events = new ArrayList<Event>();
			if (eventsInDatabase != null) {
				for (Event eventInDatabase : eventsInDatabase) {
					if (isPermitted("read:events:" + eventInDatabase.getId())) {
						events.add(eventInDatabase);
					}
				}
			}
			
			eventday.setDayNumber(i);
			eventday.setDate(sinceDay.toDate());
			eventday.setEvents(events);
			
			days.add(eventday);
		}		
		eventWeek.setDays(days);

		// Header links
		DateTime previousWeek = since.minusWeeks(1);
		DateTime nextWeek = since.plusWeeks(1);
		String headerLink = headerLink(previousWeek, nextWeek);
		response.setHeader("Link", headerLink);

		return eventWeek;
	}
	
	private String headerLink(DateTime previousWeek, DateTime nextWeek) {
		StringBuilder sb = new StringBuilder();
		sb.append("<eventWeeks/");
		sb.append(previousWeek.weekyear().get());
		sb.append("-W");
		sb.append(previousWeek.weekOfWeekyear().get() < 10 ?  "0" : "");
		sb.append(previousWeek.weekOfWeekyear().get());
		sb.append(">; rel=\"previous\"");
		
		sb.append(", ");
		
		sb.append("<eventWeeks/");
		sb.append(nextWeek.weekyear().get());
		sb.append("-W");
		sb.append(nextWeek.weekOfWeekyear().get() < 10 ?  "0" : "");
		sb.append(nextWeek.weekOfWeekyear().get());
		sb.append(">; rel=\"next\"");
		
		return sb.toString();
	}
}
