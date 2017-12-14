package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.EventTypeIn;
import se.leafcoders.rosette.controller.dto.EventTypeOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeOut;
import se.leafcoders.rosette.persistence.service.EventTypeService;
import se.leafcoders.rosette.persistence.service.ResourceTypeService;

@RestController
@RequestMapping(value = "api/eventTypes", produces = "application/json")
public class EventTypesController {

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private ResourceTypeService resourceTypeService;
    
    @GetMapping(value = "/{id}")
    public EventTypeOut getEventType(@PathVariable Long id) {
        return eventTypeService.toOut(eventTypeService.read(id, true));
    }

    @GetMapping
    public Collection<EventTypeOut> getEventTypes(HttpServletRequest request) {
        return eventTypeService.toOut(eventTypeService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EventTypeOut> postEventType(@RequestBody EventTypeIn eventType) {
        return new ResponseEntity<EventTypeOut>(eventTypeService.toOut(eventTypeService.create(eventType, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public EventTypeOut putEventType(@PathVariable Long id, HttpServletRequest request) {
        return eventTypeService.toOut(eventTypeService.update(id, EventTypeIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        return eventTypeService.delete(id, true);
    }

    // ResourceTypes

    @GetMapping(value = "/{id}/resourceTypes")
    public Collection<ResourceTypeOut> getResourceTypesOfResource(@PathVariable Long id) {
        return resourceTypeService.toOut(eventTypeService.getResourceTypes(id));
    }

    @PostMapping(value = "/{id}/resourceTypes/{resourceTypeId}", consumes = "application/json")
    public Collection<ResourceTypeOut> addResourceTypeToResource(@PathVariable Long id, @PathVariable Long resourceTypeId) {
        return resourceTypeService.toOut(eventTypeService.addResourceType(id, resourceTypeId));
    }

    @DeleteMapping(value = "/{id}/resourceTypes/{resourceTypeId}")
    public Collection<ResourceTypeOut> removeResourceTypeFromResource(@PathVariable Long id, @PathVariable Long resourceTypeId) {
        return resourceTypeService.toOut(eventTypeService.removeResourceType(id, resourceTypeId));
    }
}
