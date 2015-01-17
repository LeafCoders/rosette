package se.ryttargardskyrkan.rosette.service;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.EventType;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.ObjectReferenceOrText;
import se.ryttargardskyrkan.rosette.model.event.Event;
import se.ryttargardskyrkan.rosette.model.resource.*;

@Service
public class EventService extends MongoTemplateCRUD<Event> {
	@Autowired
	LocationService locationService;
	@Autowired
	EventTypeService eventTypeService;
	@Autowired
	ResourceTypeService resourceTypeService;
	@Autowired
	MethodsService methodsService;

	public EventService() {
		super("events", Event.class);
	}

	public List<Event> readMany(Date from, Date to) {
		Query query = new Query();
		if (from != null && to != null) {
			query.addCriteria(Criteria.where("startTime").gte(from).lt(to));
		}
		return readMany(query.with(new Sort(Sort.Direction.ASC, "startTime")));
	}

	public void assignResource(String eventId, String resourceTypeId, Resource resource, HttpServletResponse response) {
		security.checkPermission("assign:resourceTypes:" + resourceTypeId);
		security.validate(resource);

		Query query = new Query(new Criteria().andOperator(
		        Criteria.where("id").is(eventId),
		        Criteria.where("resources.resourceType.idRef").is(resourceTypeId)));		

		ResourceType resourceType = resourceTypeService.readNoDep(resource.getResourceType().getIdRef());
		Update update = methodsService.of(resource).createAssignUpdate(resourceType);

		if (mongoTemplate.updateFirst(query, update, Event.class).getN() == 0) {
			throw new NotFoundException();
		}
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	public void insertDependencies(Event data) {
		final ObjectReference<EventType> eventTypeRef = data.getEventType(); 
		if (eventTypeRef != null) {
			eventTypeRef.setReferredObject(eventTypeService.readNoDep(eventTypeRef.getIdRef()));
		}
		final ObjectReferenceOrText<Location> locationRef = data.getLocation(); 
		if (locationRef != null && locationRef.hasIdRef()) {
			locationRef.setReferredObject(locationService.readNoDep(locationRef.getIdRef()));
		}
		final List<Resource> resources = data.getResources();
		for (Resource resource : resources) {
			final ObjectReference<ResourceType> resourceTypeRef = resource.getResourceType();
			resourceTypeRef.setReferredObject(resourceTypeService.readNoDep(resourceTypeRef.getIdRef()));
			methodsService.of(resource).insertDependencies();
		}
	}
}
