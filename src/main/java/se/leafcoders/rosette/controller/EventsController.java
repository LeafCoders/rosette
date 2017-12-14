package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.leafcoders.rosette.controller.dto.EventIn;
import se.leafcoders.rosette.controller.dto.EventOut;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceRequirementIn;
import se.leafcoders.rosette.controller.dto.ResourceRequirementOut;
import se.leafcoders.rosette.persistence.service.EventService;
import se.leafcoders.rosette.persistence.service.ResourceRequirementService;
import se.leafcoders.rosette.persistence.service.ResourceService;

@RestController
@RequestMapping(value = "api/events", produces = "application/json")
public class EventsController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ResourceRequirementService resourceRequirementService;
    
    @Autowired
    private ResourceService resourceService;
    
    @GetMapping(value = "/{id}")
    public EventOut getEvent(@PathVariable Long id) {
        return eventService.toOut(eventService.read(id, true));
    }

    @GetMapping()
    public Collection<EventOut> getEvents(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "startTime");
        return eventService.toOut(eventService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EventOut> postEvent(@RequestBody EventIn event) {
        return new ResponseEntity<EventOut>(eventService.toOut(eventService.create(event, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public EventOut putEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.toOut(eventService.update(id, EventIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        return eventService.delete(id, true);
    }

    // ResourceRequirements
    
    @GetMapping(value = "/{id}/resourceRequirements")
    public Collection<ResourceRequirementOut> getResourceRequirementOfEvent(@PathVariable Long id) {
        return resourceRequirementService.toOut(eventService.getResourceRequirements(id));
    }

    @PostMapping(value = "/{id}/resourceRequirements", consumes = "application/json")
    public Collection<ResourceRequirementOut> addResourceRequirementToEvent(@PathVariable Long id, @RequestBody ResourceRequirementIn resourceRequirement) {
        return resourceRequirementService.toOut(eventService.addResourceRequirement(id, resourceRequirement.getResourceTypeId()));
    }

    @DeleteMapping(value = "/{id}/resourceRequirements/{resourceRequirementId}")
    public Collection<ResourceRequirementOut> removeResourceRequirementFromEvent(@PathVariable Long id, @PathVariable Long resourceRequirementId) {
        return resourceRequirementService.toOut(eventService.removeResourceRequirement(id, resourceRequirementId));
    }

    // ResourceRequirements / Resources

    @GetMapping(value = "/{id}/resourceRequirements/{resourceRequirementId}/resources")
    public Collection<ResourceOut> getResourcesOfRequirement(@PathVariable Long id, @PathVariable Long resourceRequirementId) {
        return resourceService.toOut(eventService.getResources(id, resourceRequirementId));
    }

    @PostMapping(value = "/{id}/resourceRequirements/{resourceRequirementId}/resources", consumes = "application/json")
    public Collection<ResourceOut> addResourceToRequirement(
        @PathVariable Long id, @PathVariable Long resourceRequirementId, @RequestParam(name = "resourceId", required = false) Long resourceId
    ) {
        return resourceService.toOut(eventService.addResource(id, resourceRequirementId, resourceId));
    }

    @DeleteMapping(value = "/{id}/resourceRequirements/{resourceRequirementId}/resources")
    public Collection<ResourceOut> removeAllResourcesFromRequirement(@PathVariable Long id, @PathVariable Long resourceRequirementId) {
        return resourceService.toOut(eventService.removeResource(id, resourceRequirementId, null));
    }
    
    @DeleteMapping(value = "/{id}/resourceRequirements/{resourceRequirementId}/resources/{resourceId}")
    public Collection<ResourceOut> removeResourceFromRequirement(
            @PathVariable Long id, @PathVariable Long resourceRequirementId, @PathVariable Long resourceId
            ) {
        return resourceService.toOut(eventService.removeResource(id, resourceRequirementId, resourceId));
    }
}
