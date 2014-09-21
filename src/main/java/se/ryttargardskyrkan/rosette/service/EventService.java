package se.ryttargardskyrkan.rosette.service;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.EventType;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.event.Event;
import se.ryttargardskyrkan.rosette.model.event.EventCreateRequest;
import se.ryttargardskyrkan.rosette.model.resource.*;

@Service
public class EventService extends MongoTemplateCRUD<Event> {
	@Autowired
	EventTypeService eventTypeService;
	@Autowired
	ResourceTypeService resourceTypeService;
	
	public EventService() {
		super("events", Event.class);
	}
	
	public Event create(EventCreateRequest eventCreateRequest, HttpServletResponse response) {
		Event event = new Event();
		event.setTitle(eventCreateRequest.getTitle());
		event.setStartTime(eventCreateRequest.getStartTime());
		event.setEndTime(eventCreateRequest.getEndTime());
		event.setEventType(eventCreateRequest.getEventType());
		addResourcesFromEventType(event);
		return create(event, response);
	}
	
	@Override
	public void insertDependencies(Event data) {
	}
	
	private void addResourcesFromEventType(Event event) {
		List<Resource> resources = new LinkedList<Resource>();
		EventType eventType = eventTypeService.readNoDep(event.getEventType().getIdRef());
		List<ObjectReference<ResourceType>> resourceTypes = eventType.getResourceTypes();
		for (ObjectReference<ResourceType> resourceType : resourceTypes) {
			resources.add(resourceTypeService.createResourceFrom(resourceType));
		}
		event.setResources(resources);
	}
}
