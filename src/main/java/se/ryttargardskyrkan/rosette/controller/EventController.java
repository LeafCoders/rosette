package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Event;
import se.ryttargardskyrkan.rosette.model.UserResource;

@Controller
public class EventController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;

	@RequestMapping(value = "events/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Event getEvent(@PathVariable String id) {
		checkPermission("read:events:" + id);
		
		Event event = mongoTemplate.findById(id, Event.class);
		if (event == null) {
			throw new NotFoundException();
		}
		return event;
	}

	@RequestMapping(value = "events", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Event> getEvents(HttpServletResponse response) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "startTime")));
		
		List<Event> eventsInDatabase = mongoTemplate.find(query, Event.class);
		List<Event> events = new ArrayList<Event>();
		if (eventsInDatabase != null) {
			for (Event eventInDatabase : eventsInDatabase) {
				if (isPermitted("read:events:" + eventInDatabase.getId())) {
					events.add(eventInDatabase);
				}
			}
		}

		return events;
	}

	@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event postEvent(@RequestBody Event event, HttpServletResponse response) {
		checkPermission("create:events");
		validate(event);

		mongoTemplate.insert(event);

		response.setStatus(HttpStatus.CREATED.value());
		return event;
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEvent(@PathVariable String id, @RequestBody Event event, HttpServletResponse response) {
		validate(event);

		Update update = new Update();

        if (isPermitted("update:events:" + id + ":title"))
            update.set("title", event.getTitle());
        if (isPermitted("update:events:" + id + ":startTime"))
            update.set("startTime", event.getStartTime());
        if (isPermitted("update:events:" + id + ":endTime"))
            update.set("endTime", event.getEndTime());
        if (isPermitted("update:events:" + id + ":description"))
            update.set("description", event.getDescription());
        if (isPermitted("update:events:" + id + ":requiredUserResourceTypes"))
            update.set("requiredUserResourceTypes", event.getRequiredUserResourceTypes());

        List<UserResource> userResources = new ArrayList<UserResource>();

        // Updating existing userResources if permitted, preventing deleting if not permitted
        Event storedEvent = mongoTemplate.findById(id, Event.class);
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
                    if (isPermitted("update:events:" + id + ":userResources:" + storedUserResource.getUserResourceTypeName())) {
                        userResources.add(updatedUserResource);
                    } else {
                        userResources.add(storedUserResource);
                    }
                } else if (!isPermitted("update:events:" + id + ":userResources:" + storedUserResource.getUserResourceTypeName())) {
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

                if (!found && isPermitted("update:events:" + id + ":userResources:" + userResource.getUserResourceTypeName())) {
                    userResources.add(userResource);
                }
            }
        }

        // TODO sort userResources

        if (userResources.isEmpty()) {
            update.set("userResources", null);
        } else {
            update.set("userResources", userResources);
        }

        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Event.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
		checkPermission("delete:events:" + id);

		Event deletedEvent = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Event.class);
		if (deletedEvent == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}
	}
}
