package se.leafcoders.rosette.controller;

import java.time.LocalDateTime;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
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
import se.leafcoders.rosette.controller.dto.ArticleOut;
import se.leafcoders.rosette.controller.dto.EventIn;
import se.leafcoders.rosette.controller.dto.EventOut;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceRequirementIn;
import se.leafcoders.rosette.controller.dto.ResourceRequirementOut;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.service.ArticleService;
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
    
    @Autowired
    private ArticleService articleService;
    
    @GetMapping(value = "/{id}")
    public EventOut getEvent(@PathVariable Long id) {
        return eventService.toOut(eventService.read(id, true));
    }

    @GetMapping()
    public Collection<EventOut> getEvents(
        @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,            
        @RequestParam(value = "before", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime before
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "startTime");
        
        Specification<Event> spec = null;
        if (from != null && before != null) {
            spec = (root, query, cb) -> {
                return cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), from),
                    cb.lessThan(root.get("startTime"), before)
                );
            };
        } else if (from != null) {
            spec = (root, query, cb) -> {
                return cb.greaterThanOrEqualTo(root.get("startTime"), from);
            };
        } else if (before != null) {
            spec = (root, query, cb) -> {
                return cb.lessThan(root.get("startTime"), before);
            };
        }

        if (spec != null) {
            return eventService.toOut(eventService.readMany(spec, sort, true));
        } else {
            return eventService.toOut(eventService.readMany(sort, true));
        }
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
    public Collection<ResourceRequirementOut> getResourceRequirementsOfEvent(@PathVariable Long id) {
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
    public Collection<ResourceOut> removeResourceFromRequirement(@PathVariable Long id, @PathVariable Long resourceRequirementId, @PathVariable Long resourceId) {
        return resourceService.toOut(eventService.removeResource(id, resourceRequirementId, resourceId));
    }
    
    // Articles
    
    @GetMapping(value = "/{id}/articles")
    public Collection<ArticleOut> getArticlesOfEvent(@PathVariable Long id) {
        return articleService.toOut(eventService.getArticles(id));
    }
    
}
