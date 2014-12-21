package se.ryttargardskyrkan.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.ryttargardskyrkan.rosette.model.event.Event;
import se.ryttargardskyrkan.rosette.service.EventService;

@Controller
public class EventController extends AbstractController {
	@Autowired
	private EventService eventService;
	@Autowired
	private MongoTemplate mongoTemplate;

	@RequestMapping(value = "events/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Event getEvent(@PathVariable String id) {
		return eventService.read(id);
	}

	@RequestMapping(value = "events", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Event> getEvents(HttpServletResponse response) {
		return eventService.readMany(new Query().with(new Sort(Sort.Direction.ASC, "startTime")));
	}

	@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event postEvent(@RequestBody Event event, HttpServletResponse response) {
		return eventService.create(event, response);
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEvent(@PathVariable String id, @RequestBody Event event, HttpServletResponse response) {
		Update update = new Update();

		update.set("title", event.getTitle());
		update.set("startTime", event.getStartTime());
		update.set("endTime", event.getEndTime());
		update.set("description", event.getDescription());
		update.set("location", event.getLocation());
		update.set("showOnPalmate", event.getShowOnPalmate());
		update.set("resources", event.getResources());

		eventService.update(id, event, update, response);
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
		eventService.delete(id, response);
	}
}
