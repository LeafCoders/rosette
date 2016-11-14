package se.leafcoders.rosette.service;

import static se.leafcoders.rosette.security.PermissionAction.READ;
import static se.leafcoders.rosette.security.PermissionAction.UPDATE;
import static se.leafcoders.rosette.security.PermissionType.EVENTS;
import static se.leafcoders.rosette.security.PermissionType.EVENTS_EVENTTYPES;
import static se.leafcoders.rosette.security.PermissionType.EVENTS_RESOURCETYPES;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.reference.LocationRefOrText;
import se.leafcoders.rosette.model.reference.UploadFileRefs;
import se.leafcoders.rosette.model.reference.UserRefsAndText;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.model.resource.UploadResource;
import se.leafcoders.rosette.model.resource.UserResource;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionResult;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.ManyQuery;
import se.leafcoders.rosette.util.QueryId;

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
		super(Event.class, EVENTS);
	}

	@Override
	protected PermissionResult permissionResultFor(PermissionAction actionType, Event event) {
	    if (event != null) {
    	    if (actionType.equals(READ)) {
    	        return checkAnyEventPermission(READ, event, null); 
    	    } else if (event.getEventType() != null) {
                return security.permissionResultFor(
                        new PermissionValue(EVENTS_EVENTTYPES, actionType, event.getEventType().getId()),
                        new PermissionValue(EVENTS, actionType, event.getId()));
            }
	    }
        return super.permissionResultFor(actionType, event);
	}

	@Override
	public Event create(Event event, HttpServletResponse response) {
		// Validate 'isPublic'
		if (event.getEventType() != null && event.getEventType().getHasPublicEvents() != null) {
			if (event.getIsPublic() == null || event.getEventType().getHasPublicEvents().getAllowChange() == false) {
				event.setIsPublic(event.getEventType().getHasPublicEvents().getValue());
			}
		} else if (event.getIsPublic() == null) {
			event.setIsPublic(false);
		}

		event.setVersion(1);
		return super.create(event, response);
	}

    public Event generate(String eventTypeId, Date startDate) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);
        calStart.set(Calendar.HOUR_OF_DAY, 11);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(startDate);
        calEnd.set(Calendar.HOUR_OF_DAY, 13);

        EventType eventType = eventTypeService.read(eventTypeId);
        Event event = new Event();
        event.setEventType(eventType);
        event.setTitle(eventType.getName());
        event.setStartTime(calStart.getTime());
        event.setEndTime(calEnd.getTime());
        event.setLocation(new LocationRefOrText());
        event.setIsPublic(eventType.getHasPublicEvents().getValue());

        List<Resource> resources = new ArrayList<Resource>();
        eventType.getResourceTypes().forEach((resourceType) -> {
            Resource resource = null;
            if (resourceType.getType().equals("user")) {
                resource = new UserResource();
                ((UserResource)resource).setUsers(new UserRefsAndText());
            } else {
                resource = new UploadResource();
                ((UploadResource)resource).setUploads(new UploadFileRefs());
            }
            resource.setType(resourceType.getType());
            resource.setResourceType(resourceType);
            resources.add(resource);
        });
        event.setResources(resources);

        return event;
    }

	public List<Event> readMany(final ManyQuery manyQuery, Date from, Date before) {
	    manyQuery.addTimeCriteria("startTime", from, before);
		return super.readMany(manyQuery);
	}

	@Override
	protected void beforeUpdate(String id, Event updateData, Event dataInDatabase) {
	    if (updateData != null && dataInDatabase != null) {
	        dataInDatabase.setVersion(dataInDatabase.getVersion() + 1);
	    }
	}

	public void assignResource(String eventId, String resourceTypeId, Resource resource, HttpServletResponse response) {
		checkAnyEventPermission(UPDATE, read(eventId, false), resourceTypeId);
		security.validate(resource);

		Query query = new Query(new Criteria().andOperator(
		        Criteria.where("id").is(QueryId.get(eventId)),
		        Criteria.where("resources.resourceType.id").is(resourceTypeId)));		

		ResourceType resourceTypeIn = resourceTypeService.read(resource.getResourceType().getId(), false);
		Update update = methodsService.of(resource).createAssignUpdate(resourceTypeIn, true);

		if (mongoTemplate.updateFirst(query, update, Event.class).getN() == 0) {
			throw notFoundException(eventId);
		}
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	public void setReferences(Event data, Event dataInDb, boolean checkPermissions) {
		if (data.getEventType() != null) {
			data.setEventType(eventTypeService.read(data.getEventType().getId(), checkPermissions));
		}
		if (data.getLocation() != null && data.getLocation().hasRef()) {
			data.getLocation().setRef(locationService.read(data.getLocation().refId(), checkPermissions));
		}
		final List<Resource> resources = data.getResources();
		if (resources != null) {
			for (Resource resource : resources) {
				resource.setResourceType(resourceTypeService.read(resource.getResourceType().getId(), checkPermissions));
				methodsService.of(resource).setReferences(checkPermissions);
			}
		}
	}
	
    @Override
	public Class<?>[] references() {
	    return new Class<?>[] { EventType.class, Location.class, ResourceType.class, User.class };
	}

	protected PermissionResult checkAnyEventPermission(PermissionAction actionType, Event event, String resourceTypeId) {
		PermissionValue eventsPermission = new PermissionValue(EVENTS, actionType, event.getId());
		PermissionValue eventsEventTypesPermission = new PermissionValue(EVENTS_EVENTTYPES, actionType, event.getEventType().getId());
		
		if (!security.isPermitted(eventsPermission, eventsEventTypesPermission)) {
			if (resourceTypeId != null) {
				return security.permissionResultFor(new PermissionValue(EVENTS_RESOURCETYPES, actionType, resourceTypeId));
			} else {
				for (Resource resource : event.getResources()) {
					if (security.isPermitted(new PermissionValue(EVENTS_RESOURCETYPES, actionType, resource.getResourceType().getId()))) {
				        return new PermissionResult();
					}
				}
			}
			return new PermissionResult(eventsPermission, eventsEventTypesPermission);
		}
		return new PermissionResult();
	}

}
