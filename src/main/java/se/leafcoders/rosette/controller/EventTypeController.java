package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.service.EventTypeService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class EventTypeController extends AbstractController {
	@Autowired
	private EventTypeService eventTypeService;

	@RequestMapping(value = "eventTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EventType getEventType(@PathVariable String id) {
		return eventTypeService.read(id);
	}

	@RequestMapping(value = "eventTypes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EventType> getEventTypes(HttpServletRequest request) {
		return eventTypeService.readMany(new ManyQuery(request, "name"));
	}

	@RequestMapping(value = "eventTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public EventType postEventType(@RequestBody EventType eventType, HttpServletResponse response) {
		return eventTypeService.create(eventType, response);
	}

	@RequestMapping(value = "eventTypes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEventType(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		eventTypeService.update(id, request, response);
	}

	@RequestMapping(value = "eventTypes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		eventTypeService.delete(id, response);
	}
}
