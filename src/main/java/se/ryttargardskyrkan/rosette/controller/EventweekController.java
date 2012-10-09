package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.model.Event;
import se.ryttargardskyrkan.rosette.model.Eventday;
import se.ryttargardskyrkan.rosette.model.Eventweek;

@Controller
public class EventweekController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public EventweekController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}
	
	@RequestMapping(value = "eventweek", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Eventweek getEventweek2(HttpServletResponse response) {
		DateTime now = DateTime.now();
		int year = now.getWeekyear();
		int week = now.getWeekOfWeekyear();
		String id = "" + year + "-W" + (week < 10 ? "0" : "") + week;
		
		return this.getEventweek(id, response);
	}

	@RequestMapping(value = "eventweek/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Eventweek getEventweek(@PathVariable String id, HttpServletResponse response) {
		int weekyear = Integer.parseInt(id.substring(0, 4));
		int weekOfWeekyear = Integer.parseInt(id.substring(6, 8));
		
		DateTime since = new DateTime();
		since = since.withWeekyear(weekyear);
		since = since.withWeekOfWeekyear(weekOfWeekyear);
		since = since.withTimeAtStartOfDay();
		since = since.withDayOfWeek(DateTimeConstants.MONDAY);
		
		Eventweek eventweek = new Eventweek();
		eventweek.setWeek(since.weekOfWeekyear().get());
		eventweek.setSince(since.toDate());
		eventweek.setUntil(since.plusDays(6).toDate());
		
		List <Eventday> days = new ArrayList<Eventday>();
		
		for (int i = 1; i <= 7; i++) {
			Eventday eventday = new Eventday();
			
			DateTime sinceDay = since.plusDays(i - 1);
			DateTime untilDay = since.plusDays(i);
			
			Query query = new Query();
			query.sort().on("startTime", Order.ASCENDING);
			Criteria criteria = new Criteria();
			criteria = Criteria.where("startTime").gte(sinceDay.toDate());
			criteria = criteria.lt(untilDay.toDate());
			query.addCriteria(criteria);
			List<Event> events = mongoTemplate.find(query, Event.class);
			
			eventday.setDayNumber(i);
			eventday.setDate(sinceDay.toDate());
			eventday.setEvents(events);
			
			days.add(eventday);
		}		
		eventweek.setDays(days);

		// Header links
		DateTime previousWeek = since.minusWeeks(1);
		DateTime nextWeek = since.plusWeeks(1);
		String headerLink = this.headerLink(previousWeek, nextWeek);
		response.setHeader("Link", headerLink);

		return eventweek;
	}
	
	String headerLink(DateTime previousWeek, DateTime nextWeek) {
		StringBuilder sb = new StringBuilder();
		sb.append("<eventweek/");
		sb.append(previousWeek.weekyear().get());
		sb.append("-W");
		sb.append(previousWeek.weekOfWeekyear().get() < 10 ?  "0" : "");
		sb.append(previousWeek.weekOfWeekyear().get());
		sb.append(">; rel=\"previous\"");
		
		sb.append(", ");
		
		sb.append("<eventweek/");
		sb.append(nextWeek.weekyear().get());
		sb.append("-W");
		sb.append(nextWeek.weekOfWeekyear().get() < 10 ?  "0" : "");
		sb.append(nextWeek.weekOfWeekyear().get());
		sb.append(">; rel=\"next\"");
		
		return sb.toString();
	}
}
