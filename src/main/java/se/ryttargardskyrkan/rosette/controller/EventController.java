package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public List<Event> getEvents() {
//		checkPermission("events:get");
		
		List<Event> events = new ArrayList<Event>();
		
		Event event1 = new Event();
		event1.setTitle("Gudstjänst 1");
		event1.setId("1");
		events.add(event1);
		
		Event event2 = new Event();
		event2.setTitle("Gudstjänst 2");
		event2.setId("2");
		events.add(event2);
				
//		List<Event> events = mongoTemplate.findAll(Event.class);
		return events;
	}

	@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event postEvent(@RequestBody Event event, HttpServletResponse response) {
		checkPermission("events:post");
		validate(event);
		
		mongoTemplate.insert(event);

		response.setStatus(HttpStatus.CREATED.value());
		return event;
	}
}
