package se.ryttargardskyrkan.rosette.service;

import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Event;

@Service
public class EventService extends MongoTemplateCRUD<Event> {

	public EventService() {
		super("events", Event.class);
	}
	
	@Override
	public void insertDependencies(Event data) {
	}
}
