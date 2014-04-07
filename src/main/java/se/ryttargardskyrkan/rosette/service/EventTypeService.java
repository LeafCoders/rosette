package se.ryttargardskyrkan.rosette.service;

import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.EventType;

@Service
public class EventTypeService extends MongoTemplateCRUD<EventType> {

	public EventTypeService() {
		super("eventTypes", EventType.class);
	}
	
	@Override
	public void insertDependencies(EventType data) {
	}
}
