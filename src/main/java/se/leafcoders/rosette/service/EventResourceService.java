package se.leafcoders.rosette.service;

import static se.leafcoders.rosette.security.PermissionAction.*;
import static se.leafcoders.rosette.security.PermissionType.EVENTS;
import static se.leafcoders.rosette.security.PermissionType.EVENTS_EVENTTYPES;
import static se.leafcoders.rosette.security.PermissionType.EVENTS_RESOURCETYPES;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.mongodb.BasicDBObject;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.QueryId;

@Service
public class EventResourceService {
    @Autowired
    EventService eventService;
	@Autowired
	ResourceTypeService resourceTypeService;
	@Autowired
	MethodsService methodsService;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    protected SecurityService security;

    public void addResource(String eventId, Resource resource, HttpServletResponse response) {
        checkAnyResourcePermission(CREATE, eventService.read(eventId, false), resource.resourceTypeId());
        security.validate(resource);

        if (resourceExist(eventId, resource.resourceTypeId())) {
            throw new SimpleValidationException(new ValidationError("resource", "resource.alreadyExists"));
        }

        setReferences(resource, true);

        Query whereQuery = new Query(Criteria.where("id").is(QueryId.get(eventId)));
        Update add = new Update().push("resources", resource); 
        mongoTemplate.upsert(whereQuery, add, Event.class);

        response.setStatus(HttpStatus.CREATED.value());
    }

	public void updateResource(String eventId, String resourceTypeId, Resource resource, HttpServletResponse response) {
		checkAnyResourcePermission(UPDATE, eventService.read(eventId, false), resourceTypeId);
		security.validate(resource);

		Query whereQuery = new Query(new Criteria().andOperator(
		        Criteria.where("id").is(QueryId.get(eventId)),
		        Criteria.where("resources.resourceType.id").is(resourceTypeId)));		

		ResourceType resourceTypeIn = resourceTypeService.read(resource.getResourceType().getId(), false);
		Update updateQuery = methodsService.of(resource).updateQuery(resourceTypeIn, true);

		if (mongoTemplate.updateFirst(whereQuery, updateQuery, Event.class).getN() == 0) {
			throw new NotFoundException("resourceType", resourceTypeId);
		}
		response.setStatus(HttpStatus.OK.value());
	}

    public void removeResource(String eventId, String resourceTypeId, HttpServletResponse response) {
        checkAnyResourcePermission(DELETE, eventService.read(eventId, false), resourceTypeId);

        if (!resourceExist(eventId, resourceTypeId)) {
            throw new SimpleValidationException(new ValidationError("resource", "resource.doesNotExists"));
        }

        Query whereQuery = new Query(new Criteria().andOperator(
                Criteria.where("id").is(QueryId.get(eventId)),
                Criteria.where("resources.resourceType.id").is(resourceTypeId)));
        Update remove = new Update().pull("resources", new BasicDBObject("resourceType.id", resourceTypeId)); 
        mongoTemplate.upsert(whereQuery, remove, Event.class);

        response.setStatus(HttpStatus.OK.value());
    }

    private void checkAnyResourcePermission(PermissionAction actionType, Event event, String resourceTypeId) {
        security.permissionResultFor(
            new PermissionValue(EVENTS, actionType, event.getId()),
            new PermissionValue(EVENTS_RESOURCETYPES, actionType, resourceTypeId),
            new PermissionValue(EVENTS_EVENTTYPES, actionType, event.getEventType().getId())
        ).checkAndThrow();
    }

    private boolean resourceExist(String eventId, String resourceTypeId) {
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("id").is(QueryId.get(eventId)),
                Criteria.where("resources.resourceType.id").is(resourceTypeId)));       
        return mongoTemplate.count(query, Event.class) > 0;
    }

    public void setReferences(Resource resource, boolean checkPermissions) {
        resource.setResourceType(resourceTypeService.read(resource.getResourceType().getId(), checkPermissions));
        methodsService.of(resource).setReferences(checkPermissions);
    }
}
