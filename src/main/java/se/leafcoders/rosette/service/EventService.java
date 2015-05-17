package se.leafcoders.rosette.service;

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
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.model.resource.ResourceType;
import static se.leafcoders.rosette.security.PermissionAction.*;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionCheckFilter;
import se.leafcoders.rosette.security.PermissionValue;
import static se.leafcoders.rosette.security.PermissionType.*;
import util.QueryId;

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
		super(Event.class, EVENTS, PermissionCheckFilter.NONE);
	}

	@Override
	public Event create(Event event, HttpServletResponse response) {
		if (event.getEventType() != null) {
			security.checkPermission(
					new PermissionValue(EVENTS_EVENTTYPES, CREATE, event.getEventType().getId()),
					new PermissionValue(EVENTS, CREATE));
		} else {
			checkPermission(CREATE);
		}
		return super.create(event, response);
	}

	@Override
	public Event read(String id) {
		Event event = super.read(id);
		checkAnyEventPermission(READ, event, null);
		return event;
	}
	
	public List<Event> readMany(Date from, Date to) {
		Query query = new Query();
		if (from != null && to != null) {
			query.addCriteria(Criteria.where("startTime").gte(from).lt(to));
		}

		return super.readMany(query.with(new Sort(Sort.Direction.ASC, "startTime")));
	}

	@Override
	public boolean readManyItemFilter(Event event) {
		try {
			checkAnyEventPermission(READ, event, null);
		} catch (Exception ignore) {
			return false;
		}
		return true;
	}

	@Override
	public void update(String eventId, Event updateData, HttpServletResponse response) {
		checkEventTypesPermission(UPDATE, readWithoutPermission(eventId));
		super.update(eventId, updateData, response);
	}
	
	@Override
	public void delete(String eventId, HttpServletResponse response) {
		checkEventTypesPermission(DELETE, readWithoutPermission(eventId));
		super.delete(eventId, response);
	}

	public void assignResource(String eventId, String resourceTypeId, Resource resource, HttpServletResponse response) {
		checkAnyEventPermission(UPDATE, readWithoutPermission(eventId), resourceTypeId);
		security.validate(resource);

		Query query = new Query(new Criteria().andOperator(
		        Criteria.where("id").is(QueryId.get(eventId)),
		        Criteria.where("resources.resourceType.id").is(resourceTypeId)));		

		ResourceType resourceTypeIn = resourceTypeService.readWithoutPermission(resource.getResourceType().getId());
		Update update = methodsService.of(resource).createAssignUpdate(resourceTypeIn);

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
		if (resources != null) {
			for (Resource resource : resources) {
				resource.setResourceType(resourceTypeService.read(resource.getResourceType().getId()));
				methodsService.of(resource).insertDependencies();
			}
		}
	}

	protected void checkEventTypesPermission(PermissionAction actionType, Event event) {
		security.checkPermission(
				new PermissionValue(EVENTS, actionType, event.getId()),
				new PermissionValue(EVENTS_EVENTTYPES, actionType, event.getEventType().getId()));
	}

	protected void checkAnyEventPermission(PermissionAction actionType, Event event, String resourceTypeId) {
		PermissionValue eventsPermission = new PermissionValue(EVENTS, actionType, event.getId());
		PermissionValue eventsEventTypesPermission = new PermissionValue(EVENTS_EVENTTYPES, actionType, event.getEventType().getId());
		
		if (!security.isPermitted(eventsPermission, eventsEventTypesPermission)) {
			if (resourceTypeId != null) {
				security.checkPermission(new PermissionValue(EVENTS_RESOURCETYPES, actionType, resourceTypeId));
				return;
			} else {
				for (Resource resource : event.getResources()) {
					if (security.isPermitted(new PermissionValue(EVENTS_RESOURCETYPES, actionType, resource.getResourceType().getId()))) {
						return;
					}
				}
			}
			security.throwPermissionMissing(eventsPermission, eventsEventTypesPermission);
		}
	}

}
