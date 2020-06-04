package se.leafcoders.rosette.endpoint.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.exception.NotFoundException;
import se.leafcoders.rosette.core.permission.PermissionAction;
import se.leafcoders.rosette.core.permission.PermissionId;
import se.leafcoders.rosette.core.permission.PermissionValue;
import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.core.service.SseService;
import se.leafcoders.rosette.endpoint.article.Article;
import se.leafcoders.rosette.endpoint.article.ArticleRepository;
import se.leafcoders.rosette.endpoint.eventtype.EventTypePermissionValue;
import se.leafcoders.rosette.endpoint.eventtype.EventTypeRefOut;
import se.leafcoders.rosette.endpoint.eventtype.EventTypeService;
import se.leafcoders.rosette.endpoint.resource.Resource;
import se.leafcoders.rosette.endpoint.resource.ResourceService;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypePermissionValue;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeService;

@Service
public class EventService extends PersistenceService<Event, EventIn, EventOut> {

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    @Autowired
    private ResourceRequirementService resourceRequirementService;

    @Autowired
    private ResourceRequirementRepository resourceRequirementRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SseService sseService;

    public EventService(EventRepository repository) {
        super(Event.class, EventPermissionValue::new, repository);
    }

    private EventRepository repo() {
        return (EventRepository) repository;
    }

    @Override
    public List<PermissionValue> itemCreatePermissions(EventIn eventIn) {
        List<PermissionValue> permissions = super.itemCreatePermissions(eventIn);
        permissions.add(new EventTypePermissionValue().createEvents().forId(eventIn.getEventTypeId()));
        return permissions;
    }

    @Override
    public List<PermissionValue> itemReadUpdateDeletePermissions(PermissionAction permissionAction,
            PermissionId<Event> permissionId) {
        Event event = permissionId.getItem();
        Long eventTypeId = event != null ? event.getEventTypeId() : null;

        List<PermissionValue> permissions = new ArrayList<>();
        permissions.add(new EventPermissionValue().action(permissionAction).forId(permissionId.getId()));

        if (permissionAction == PermissionAction.CREATE) {
            permissions.add(new EventTypePermissionValue().createEvents().forId(eventTypeId));
        } else if (permissionAction == PermissionAction.READ) {
            List<ResourceType> resourceTypes = event != null ? event.getResourceRequirements().stream()
                    .map(rr -> rr.getResourceType()).collect(Collectors.toList()) : null;
            permissions.add(new EventTypePermissionValue().readEvents().forId(eventTypeId));
            permissions.add(new ResourceTypePermissionValue().readEvents().forPersistables(resourceTypes));
            permissions.add(new ResourceTypePermissionValue().assignEventResources().forPersistables(resourceTypes));
        } else if (permissionAction == PermissionAction.UPDATE) {
            permissions.add(new EventTypePermissionValue().updateEvents().forId(eventTypeId));
        } else if (permissionAction == PermissionAction.DELETE) {
            permissions.add(new EventTypePermissionValue().deleteEvents().forId(eventTypeId));
        }
        return permissions;
    }

    @Override
    public Event create(EventIn itemIn, boolean checkPermissions) {
        Event event = super.create(itemIn, checkPermissions);
        event.setResourceRequirements(event.getEventType().getResourceTypes().stream().map(resourceType -> {
            return new ResourceRequirement(event, resourceType);
        }).collect(Collectors.toSet()));
        try {
            Event savedEvent = repository.save(event);
            pushChangedEvent(savedEvent.getId());
            return savedEvent;
        } catch (Exception ignore) {
            throw new ForbiddenException(ApiError.UNKNOWN_REASON);
        }
    }

    @Override
    public Event update(Long id, Class<EventIn> inClass, HttpServletRequest request, boolean checkPermissions) {
        Event updatedEvent = super.update(id, inClass, request, checkPermissions);
        pushChangedEvent(updatedEvent.getId());
        return updatedEvent;
    }

    @Override
    protected Event convertFromInDTO(EventIn dto, JsonNode rawIn, Event item) {
        if (rawIn == null || rawIn.has("startTime")) {
            item.setStartTime(dto.getStartTime());
        }
        if (rawIn == null || rawIn.has("endTime")) {
            item.setEndTime(dto.getEndTime());
        }
        if (rawIn == null || rawIn.has("title")) {
            item.setTitle(dto.getTitle());
        }
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        if (rawIn == null || rawIn.has("privateDescription")) {
            item.setPrivateDescription(dto.getPrivateDescription());
        }
        if (rawIn == null || rawIn.has("eventTypeId")) {
            item.setEventType(eventTypeService.read(dto.getEventTypeId(), true));
        }
        if (rawIn == null || rawIn.has("isPublic")) {
            item.setIsPublic(dto.getIsPublic());
        }
        return item;
    }

    @Override
    protected EventOut convertToOutDTO(Event item) {
        EventOut dto = new EventOut();
        dto.setId(item.getId());
        dto.setStartTime(item.getStartTime());
        dto.setEndTime(item.getEndTime());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setPrivateDescription(item.getPrivateDescription());
        dto.setEventType(eventTypeService.toOutRef(item.getEventType(), EventTypeRefOut::new));
        dto.setIsPublic(item.getIsPublic());
        dto.setResourceRequirements(resourceRequirementService.toOut(item.getResourceRequirements()));
        return dto;
    }

    public List<Event> readForCalendar(List<Long> eventTypeIds, LocalDateTime afterTime, LocalDateTime beforeTime) {
        return repo().findForCalendar(eventTypeIds, afterTime, beforeTime);
    }

    public Set<ResourceRequirement> getResourceRequirements(Long eventId) {
        return read(eventId, true).getResourceRequirements();
    }

    public Set<ResourceRequirement> addResourceRequirement(Long eventId, Long resourceTypeId) {
        Event event = read(eventId, true);
        ResourceType resourceType = resourceTypeService.read(resourceTypeId, true);
        checkModifyResourceRequirementPermission(event, resourceTypeId);

        ResourceRequirement resourceRequirement = new ResourceRequirement(event, resourceType);
        event.addResourceRequirement(resourceRequirement);
        try {
            Set<ResourceRequirement> resourceRequirements = repository.save(event).getResourceRequirements();
            pushChangedEvent(eventId);
            return resourceRequirements;
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public Set<ResourceRequirement> removeResourceRequirement(Long eventId, Long resourceRequirementId) {
        Event event = read(eventId, true);
        ResourceRequirement resourceRequirement = resourceRequirementService.read(resourceRequirementId);
        checkModifyResourceRequirementPermission(event, resourceRequirement.getResourceType().getId());

        resourceRequirement.getResourceType().getResources();
        event.removeResourceRequirement(resourceRequirement);

        Set<ResourceRequirement> resourceRequirements = repository.save(event).getResourceRequirements();
        pushChangedEvent(eventId);
        return resourceRequirements;
    }

    private void checkModifyResourceRequirementPermission(Event event, Long resourceTypeId) {
        checkAnyPermission(new EventPermissionValue().update().forPersistable(event),
                new EventTypePermissionValue().updateEvents().forId(event.getEventTypeId()),
                new EventTypePermissionValue().modifyEventResourceRequirements().forId(event.getEventTypeId()),
                new ResourceTypePermissionValue().modifyEventResourceRequirement().forId(resourceTypeId));
    }

    private void checkAssignResourceRequirementPermission(Event event, Long resourceTypeId) {
        checkAnyPermission(new EventPermissionValue().update().forPersistable(event),
                new EventTypePermissionValue().updateEvents().forId(event.getEventTypeId()),
                new EventTypePermissionValue().assignEventResources().forId(event.getEventTypeId()),
                new ResourceTypePermissionValue().assignEventResources().forId(resourceTypeId));
    }

    private ResourceRequirement getResourceRequirement(Long eventId, Long resourceRequirementId) {
        Optional<ResourceRequirement> rr = getResourceRequirements(eventId).stream()
                .filter(item -> item.getId().equals(resourceRequirementId)).findFirst();
        if (rr.isPresent()) {
            return rr.get();
        }
        throw new NotFoundException(ResourceRequirement.class, resourceRequirementId);
    }

    public Set<Resource> getResources(Long eventId, Long resourceRequirementId) {
        return getResourceRequirement(eventId, resourceRequirementId).getResources();
    }

    public Set<Resource> addResource(Long eventId, Long resourceRequirementId, Long resourceId) {
        Event event = read(eventId, true);
        ResourceRequirement resourceRequirement = getResourceRequirement(eventId, resourceRequirementId);
        checkAssignResourceRequirementPermission(event, resourceRequirement.getResourceType().getId());

        if (resourceId != null) {
            Resource resource = resourceService.read(resourceId, true);
            if (!resource.getResourceTypes().contains(resourceRequirement.getResourceType())) {
                throw ForbiddenException.dontBelongsTo(Resource.class, resourceId, ResourceType.class,
                        resourceRequirement.getResourceType().getId());
            }
            resourceRequirement.addResource(resource);
            resourceService.updateUsage(resource);
        } else {
            resourceRequirement.setResources(new HashSet<>(resourceRequirement.getResourceType().getResources()));
        }
        try {
            Set<Resource> resources = resourceRequirementRepository.save(resourceRequirement).getResources();
            pushChangedEvent(eventId);
            return resources;
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public Set<Resource> removeResource(Long eventId, Long resourceRequirementId, Long resourceId) {
        Event event = read(eventId, true);
        ResourceRequirement resourceRequirement = getResourceRequirement(eventId, resourceRequirementId);
        checkAssignResourceRequirementPermission(event, resourceRequirement.getResourceType().getId());

        if (resourceId != null) {
            Resource resource = resourceService.read(resourceId, true);
            resourceRequirement.removeResource(resource);
        } else {
            resourceRequirement.setResources(null);
        }
        Set<Resource> resources = resourceRequirementRepository.save(resourceRequirement).getResources();
        pushChangedEvent(eventId);
        return resources;
    }

    public List<Article> getArticles(Long eventId) {
        // Check permission with a read
        read(eventId, true);
        return articleRepository.findByEventId(eventId);
    }

    private void pushChangedEvent(Long eventId) {
        sseService.sendEvent(toOut(read(eventId, false)));
    }

}
