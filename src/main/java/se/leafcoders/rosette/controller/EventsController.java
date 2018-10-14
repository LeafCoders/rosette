package se.leafcoders.rosette.controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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
import se.leafcoders.rosette.controller.dto.EventsPublicOut;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceRequirementIn;
import se.leafcoders.rosette.controller.dto.ResourceRequirementOut;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.ResourceRequirement;
import se.leafcoders.rosette.persistence.service.ArticleService;
import se.leafcoders.rosette.persistence.service.EventService;
import se.leafcoders.rosette.persistence.service.ResourceRequirementService;
import se.leafcoders.rosette.persistence.service.ResourceService;

@Transactional
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
        if (from == null && before == null) {
            from = LocalDateTime.now();
        }
        Optional<LocalDateTime> fromOptional = Optional.ofNullable(from);
        Optional<LocalDateTime> beforeOptional = Optional.ofNullable(before);

        Sort sort = new Sort(Sort.Direction.ASC, "startTime");
        Specification<Event> spec = (root, query, cb) -> {
            // FETCH two levels deep and sort out duplicates (distinct)
            query.distinct(true);
            Fetch<Event, ResourceRequirement> rrFetch = root.fetch("resourceRequirements", JoinType.LEFT);
            rrFetch.fetch("resources", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();
            fromOptional.ifPresent(time -> predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), time)));
            beforeOptional.ifPresent(time -> predicates.add(cb.lessThan(root.get("startTime"), time)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return eventService.toOut(eventService.readMany(spec, sort, true));
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

    // Public

    @GetMapping(value = "/public")
    public EventsPublicOut getPublicEvents(@RequestParam(required = true) String rangeMode, @RequestParam Integer rangeOffset) {
        rangeOffset = rangeOffset != null ? rangeOffset : 0;
        LocalDateTime from;
        LocalDateTime before;
        if (rangeMode.toLowerCase().contains("week")) {
            from = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).with(DayOfWeek.MONDAY).plusWeeks(rangeOffset);
            before = from.plusDays(7);
        } else {
            from = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(rangeOffset);
            before = from.plusMonths(1);
        }
        
        Sort sort = new Sort(Sort.Direction.ASC, "startTime");

        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Optional.ofNullable(from).ifPresent(time -> predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), time)));
            Optional.ofNullable(before).ifPresent(time -> predicates.add(cb.lessThan(root.get("startTime"), time)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };        
        List<Event> publicEvents = eventService.readMany(spec, sort, false).stream().filter(e -> e.getIsPublic()).collect(Collectors.toList());
        return new EventsPublicOut(from, before, publicEvents);
    }

}
