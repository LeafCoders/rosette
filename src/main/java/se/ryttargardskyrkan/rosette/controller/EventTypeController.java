package se.ryttargardskyrkan.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.EventType;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EventTypeController extends AbstractController {
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "eventTypes/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public EventType getEventType(@PathVariable String id) {
        checkPermission("read:eventTypes:" + id);

        EventType eventType = mongoTemplate.findById(id, EventType.class);
        if (eventType == null) {
            throw new NotFoundException();
        }
        return eventType;
    }

    @RequestMapping(value = "eventTypes", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<EventType> getEventTypes() {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));

        List<EventType> eventTypesInDatabase = mongoTemplate.find(query, EventType.class);
        List<EventType> eventTypes = new ArrayList<EventType>();
        if (eventTypesInDatabase != null) {
            for (EventType eventTypeInDatabase : eventTypesInDatabase) {
                if (isPermitted("read:eventTypes:" + eventTypeInDatabase.getId())) {
                    eventTypes.add(eventTypeInDatabase);
                }
            }
        }
        return eventTypes;
    }

    @RequestMapping(value = "eventTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EventType postEventType(@RequestBody EventType eventType, HttpServletResponse response) {
        checkPermission("create:eventTypes");
        validate(eventType);

        mongoTemplate.insert(eventType);

        response.setStatus(HttpStatus.CREATED.value());
        return eventType;
    }

    @RequestMapping(value = "eventTypes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putEventType(@PathVariable String id, @RequestBody EventType eventType, HttpServletResponse response) {
        checkPermission("update:eventTypes:" + id);
        validate(eventType);

        Update update = new Update();
        update.set("name", eventType.getName());

        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, EventType.class).getN() == 0) {
            throw new NotFoundException();
        }

        response.setStatus(HttpStatus.OK.value());
    }

    @RequestMapping(value = "eventTypes/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
        checkPermission("delete:eventTypes:" + id);

        EventType deletedEventType = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), EventType.class);
        if (deletedEventType == null) {
            throw new NotFoundException();
        } else {
            response.setStatus(HttpStatus.OK.value());
        }
    }
}
