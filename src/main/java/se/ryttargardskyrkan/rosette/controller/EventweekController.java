package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
		String id = "" + year + "-" + (week < 10 ? "0" : "") + week;
		
		return this.getEventweek(id, response);
	}

	@RequestMapping(value = "eventweek/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Eventweek getEventweek(@PathVariable String id, HttpServletResponse response) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-ww");
		DateTime since = fmt.parseDateTime(id);
		
		Eventweek eventweek = new Eventweek();
		eventweek.setWeek(since.weekOfWeekyear().get());
		
		int monthOfFirstDay = since.getMonthOfYear();
		int monthOfLastDay = since.plusDays(6).getMonthOfYear();
		List<Integer> months = new ArrayList<Integer>();
		months.add(monthOfFirstDay);
		if (monthOfLastDay != monthOfFirstDay) {
			months.add(monthOfLastDay);	
		}
		eventweek.setMonths(months);
		
		Map<Integer, Eventday> days = new HashMap<Integer, Eventday>();
		
		for (int i = 1; i <= 7; i++) {
			Eventday eventday = new Eventday();
			
			DateTime sinceDay = since.plusDays(i - 1);
			DateTime untilDay = since.plusDays(i);
			
			Query query = new Query();
			query.sort().on("startTime", Order.ASCENDING);
			Criteria criteria = new Criteria();
			criteria = Criteria.where("startTime").gte(sinceDay.toDate());
			criteria = criteria.lte(untilDay.toDate());
			query.addCriteria(criteria);
			List<Event> events = mongoTemplate.find(query, Event.class);
			
			eventday.setDate(sinceDay.toDate());
			eventday.setEvents(events);
			
			days.put(i, eventday);
		}		
		eventweek.setDays(days);
		
		// Header links
		StringBuilder sb = new StringBuilder();
		sb.append("<eventweek/2012-01>; rel=\"previous\"");
		sb.append(",");
		sb.append("<eventweek/2012-02>; rel=\"next\"");
		response.setHeader("Link", sb.toString());

		return eventweek;
	}
}
