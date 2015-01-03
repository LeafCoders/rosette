package se.ryttargardskyrkan.rosette.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.EventType;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.ObjectReferenceOrText;
import se.ryttargardskyrkan.rosette.model.ObjectReferencesAndText;
import se.ryttargardskyrkan.rosette.model.UploadResponse;
import se.ryttargardskyrkan.rosette.model.User;
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
	UserService userService;
	@Autowired
	UploadService uploadService;

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
			if (resource instanceof UserResource) {
				UserResource userResource = (UserResource) resource;
				final ObjectReferencesAndText<User> userRefs = userResource.getUsers();
				if (userRefs != null) {
					for (ObjectReference<User> userRef : userRefs.getRefs()) {
						userRef.setReferredObject(userService.readNoDep(userRef.getIdRef()));
					}
				}
			} else if (resource instanceof UploadResource) {
				UploadResource uploadResource = (UploadResource) resource;
				final List<ObjectReference<UploadResponse>> uploadRefs = uploadResource.getUploads();
				if (uploadRefs != null) {
					for (ObjectReference<UploadResponse> uploadRef : uploadRefs) {
						uploadRef.setReferredObject(uploadService.read(uploadRef.getIdRef()));
					}
				}
			}
		}
	}
}
