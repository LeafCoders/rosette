package se.ryttargardskyrkan.rosette.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.model.Event;

@Controller
public class EventController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public EventController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(value = "events", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Event> getEvents(
			@RequestParam(required = false) String since,
			@RequestParam(required = false) String until) {
		Query query = new Query();

		if (since != null)
			query.addCriteria(Criteria.where("startTime").gte(new Date(Long.parseLong(since))));

		if (until != null)
			query.addCriteria(Criteria.where("startTime").lte(new Date(Long.parseLong(until))));
		
		List<Event> events = mongoTemplate.find(query, Event.class);

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
	
	@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE)
	public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
		checkPermission("events:delete");

		mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Event.class);

		response.setStatus(HttpStatus.OK.value());
	}
}
