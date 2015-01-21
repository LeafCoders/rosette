package se.ryttargardskyrkan.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.EventType;
import se.ryttargardskyrkan.rosette.model.resource.ResourceType;

@Service
public class EventTypeService extends MongoTemplateCRUD<EventType> {

	@Autowired
	ResourceTypeService resourceTypeService;

	public EventTypeService() {
		super("eventTypes", EventType.class);
	}

	@Override
	public EventType create(EventType data, HttpServletResponse response) {
		validateUniqueId(data);
		return super.create(data, response);
	}

	@Override
	public void insertDependencies(EventType data) {
		if (data.getResourceTypes() != null) {
			for (ResourceType resourceType : data.getResourceTypes()) {
				resourceType = resourceTypeService.read(resourceType.getId());
			}
		}
	}
}
