package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.ryttargardskyrkan.rosette.model.event.Event;
import se.ryttargardskyrkan.rosette.model.event.EventCreateRequest;
import se.ryttargardskyrkan.rosette.model.resource.UserResource;
import se.ryttargardskyrkan.rosette.model.resource.UserResourceType;
import se.ryttargardskyrkan.rosette.service.EventService;

@Controller
public class EventController extends AbstractController {
	@Autowired
	private EventService eventService;
	@Autowired
	private MongoTemplate mongoTemplate;

	@RequestMapping(value = "events/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Event getEvent(@PathVariable String id) {
		return eventService.read(id);
	}

	@RequestMapping(value = "events", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Event> getEvents(HttpServletResponse response) {
		return eventService.readMany(new Query().with(new Sort(Sort.Direction.ASC, "startTime")));
	}

	@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event postEvent(@RequestBody EventCreateRequest eventCreateRequest, HttpServletResponse response) {
		return eventService.create(eventCreateRequest, response);
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEvent(@PathVariable String id, @RequestBody Event event, HttpServletResponse response) {
		Update update = new Update();

		if (isPermitted("update:events:" + id + ":title"))
			update.set("title", event.getTitle());
		if (isPermitted("update:events:" + id + ":startTime"))
			update.set("startTime", event.getStartTime());
		if (isPermitted("update:events:" + id + ":endTime"))
			update.set("endTime", event.getEndTime());
		if (isPermitted("update:events:" + id + ":description"))
			update.set("description", event.getDescription());
		if (isPermitted("update:events:" + id + ":eventType"))
			update.set("eventType", event.getEventType());
		if (isPermitted("update:events:" + id + ":location"))
			update.set("location", event.getLocation());
		if (isPermitted("update:events:" + id + ":requiredUserResourceTypes"))
			update.set("requiredUserResourceTypes", null);//event.getRequiredUserResourceTypes());
		update.set("userResources", updatedUserResourcesForEvent(id, event));

		eventService.update(id, event, update, response);
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
		eventService.delete(id, response);
	}

	private List<UserResource> updatedUserResourcesForEvent(String id, Event event) {
		List<UserResource> userResources = new ArrayList<UserResource>();

		// Updating existing userResources if permitted, preventing deleting if not permitted
		Event storedEvent = mongoTemplate.findById(id, Event.class);
/* TODO: Fix errors in code...
		if (storedEvent != null && storedEvent.getUserResources() != null) {
			for (UserResource storedUserResource : storedEvent.getUserResources()) {
				UserResource updatedUserResource = null;
				for (UserResource userResource : event.getUserResources()) {
					if (storedUserResource.getUserResourceTypeId().equals(userResource.getUserResourceTypeId())) {
						updatedUserResource = userResource;
						break;
					}
				}

				if (updatedUserResource != null) {
					if (isPermitted("update:events:" + id + ":userResources:" + storedUserResource.getUserResourceTypeId())) {
						userResources.add(updatedUserResource);
					} else {
						userResources.add(storedUserResource);
					}
				} else if (!isPermitted("update:events:" + id + ":userResources:" + storedUserResource.getUserResourceTypeId())) {
					userResources.add(storedUserResource);
				}
			}
		}

		// Adding new userResources if permitted
		if (event.getUserResources() != null) {
			for (UserResource userResource : event.getUserResources()) {
				boolean found = false;
				if (storedEvent != null && storedEvent.getUserResources() != null) {
					for (UserResource storedUserResource : storedEvent.getUserResources()) {
						if (userResource.getUserResourceTypeId().equals(storedUserResource.getUserResourceTypeId())) {
							found = true;
							break;
						}
					}
				}

				if (!found && isPermitted("update:events:" + id + ":userResources:" + userResource.getUserResourceTypeId())) {
					userResources.add(userResource);
				}
			}
		}
*/
		List<UserResource> sortedUserResources = sortUserResources(userResources);

		return sortedUserResources;
	}

	private List<UserResource> sortUserResources(List<UserResource> userResources) {
		List<UserResource> sortedUserResources = null;

		if (userResources != null && !userResources.isEmpty()) {
			sortedUserResources = new ArrayList<UserResource>();

			Query query = new Query();
			query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "sortOrder")));
			List<UserResourceType> userResourceTypes = mongoTemplate.find(query, UserResourceType.class);
/* TODO: Fix errors in code...
			for (UserResourceType userResourceType : userResourceTypes) {
				for (UserResource userResource : userResources) {
					if (userResourceType.getId().equals(userResource.getUserResourceTypeId())) {
						sortedUserResources.add(userResource);
						break;
					}
				}
			}
*/
		}

		return sortedUserResources;
	}
}
