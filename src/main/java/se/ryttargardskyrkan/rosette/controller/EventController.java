package se.ryttargardskyrkan.rosette.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Event;

@Controller
public class EventController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public EventController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}
	
	@RequestMapping(value = "events/{id}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event getEvent(@PathVariable String id) {
		Event event = mongoTemplate.findById(id, Event.class);
		if (event == null) {
			throw new NotFoundException();
		}
		return event;
	}

	@RequestMapping(value = "events", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Event> getEvents(
			@RequestParam(required = false) String since,
			@RequestParam(required = false) String until,
			@RequestParam(required = false) String themeId,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer per_page,
			HttpServletResponse response) {
		Query query = new Query();
		query.sort().on("startTime", Order.ASCENDING);
		
		int thePage = 1;
		int thePerPage = 20;
		
		if (page != null && page > 0) {
			thePage = page;
		}
		if (per_page != null && per_page > 0) {
			thePerPage = per_page;
		}		
		
		query.limit(thePerPage);
		query.skip((thePage - 1) * thePerPage);
		
		Criteria criteria = new Criteria();
		if (since != null) {
			criteria = Criteria.where("startTime").gte(new Date(Long.parseLong(since)));
		} else if (themeId == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			criteria = Criteria.where("startTime").gte(calendar.getTime());
		}
		if (until != null) {
			criteria = criteria.lte(new Date(Long.parseLong(until)));
		}		
		query.addCriteria(criteria);

		if (themeId != null) {
			query.addCriteria(Criteria.where("themeId").is(themeId));
		}
		
		// Events
		List<Event> events = mongoTemplate.find(query, Event.class);
		
		// Header links
		Query queryForLinks = new Query();
		queryForLinks.addCriteria(criteria);
		long numberOfEvents = mongoTemplate.count(queryForLinks, Event.class);
		
		if (numberOfEvents > 0) {
			StringBuilder sb = new StringBuilder();
			String delimiter = "";
			
			if (thePage - 1 > 0) {
				sb.append(delimiter);
				sb.append("<events?page=" + (thePage - 1));
				if (per_page != null) {
					sb.append("&per_page=" + per_page);
				}
				if (since != null) {
					sb.append("&since=" + since);
				}
				if (until != null) {
					sb.append("&until=" + until);
				}
				
				sb.append(">; rel=\"previous\"");
				delimiter = ",";
			}
			
			if (numberOfEvents > thePage * thePerPage) {
				sb.append(delimiter);
				sb.append("<events?page=" + (thePage + 1));
				if (per_page != null) {
					sb.append("&per_page=" + per_page);
				}
				if (since != null) {
					sb.append("&since=" + since);
				}
				if (until != null) {
					sb.append("&until=" + until);
				}
				sb.append(">; rel=\"next\"");
				delimiter = ",";
			}
			
			response.setHeader("Link", sb.toString());
		}

		return events;
	}

	@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event postEvent(@RequestBody Event event, HttpServletResponse response) {
		checkPermission("events:create");
		validate(event);

		mongoTemplate.insert(event);

		response.setStatus(HttpStatus.CREATED.value());
		return event;
	}
	
	@RequestMapping(value = "events/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEvent(@PathVariable String id, @RequestBody Event event, HttpServletResponse response) {
		checkPermission("events:update");
		validate(event);

		Update update = new Update();
		if (event.getTitle() != null)
			update.set("title", event.getTitle());
		if (event.getStartTime() != null)
			update.set("startTime", event.getStartTime());
		if (event.getEndTime() != null)
			update.set("endTime", event.getEndTime());
		
		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Event.class).getN() == 0) {
			throw new NotFoundException();
		}
		
		response.setStatus(HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE)
	public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
		checkPermission("events:delete:" + id);

		Event deletedEvent = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Event.class);
		if (deletedEvent == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());	
		}
	}
}
