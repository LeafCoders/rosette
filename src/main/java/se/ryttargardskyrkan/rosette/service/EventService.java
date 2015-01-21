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
import se.ryttargardskyrkan.rosette.model.event.Event;
import se.ryttargardskyrkan.rosette.model.resource.Resource;
import se.ryttargardskyrkan.rosette.model.resource.ResourceType;

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
		        Criteria.where("resources.resourceType.id").is(resourceTypeId)));		

		ResourceType resourceType = resourceTypeService.readNoDep(resource.getResourceType().getId());
		Update update = methodsService.of(resource).createAssignUpdate(resourceType);

		if (mongoTemplate.updateFirst(query, update, Event.class).getN() == 0) {
			throw new NotFoundException();
		}
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	public void insertDependencies(Event data) {
		if (data.getEventType() != null) {
			data.setEventType(eventTypeService.read(data.getEventType().getId()));
		}
		if (data.getLocation() != null && data.getLocation().hasRef()) {
			data.getLocation().setRef(locationService.read(data.getLocation().refId()));
		}
		final List<Resource> resources = data.getResources();
		for (Resource resource : resources) {
			resource.setResourceType(resourceTypeService.read(resource.getResourceType().getId()));
			methodsService.of(resource).insertDependencies();
		}
	}
}
