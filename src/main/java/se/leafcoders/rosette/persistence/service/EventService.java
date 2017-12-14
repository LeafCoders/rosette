package se.leafcoders.rosette.persistence.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.EventIn;
import se.leafcoders.rosette.controller.dto.EventOut;
import se.leafcoders.rosette.controller.dto.EventTypeRefOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionId;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.model.ResourceRequirement;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.EventRepository;
import se.leafcoders.rosette.persistence.repository.ResourceRequirementRepository;

@Service
public class EventService extends PersistenceService<Event, EventIn, EventOut> {

    @Autowired
    EventTypeService eventTypeService;

    @Autowired
    ResourceTypeService resourceTypeService;

    @Autowired
    ResourceRequirementService resourceRequirementService;
    
    @Autowired
    ResourceRequirementRepository resourceRequirementRepository;
    
    @Autowired
    ResourceService resourceService;
    
    public EventService(EventRepository repository) {
        super(Event.class, PermissionType.EVENTS, repository);
    }

    @Override
    public List<PermissionValue> itemPermissions(PermissionAction actionType, PermissionId<Event> permissionId) {
        Event event = permissionId.getItem();
        return Arrays.asList(
            permissionValue(PermissionType.EVENTS, actionType).forId(permissionId.getId()),
            permissionValue(PermissionType.EVENTS_BY_EVENT_TYPES, actionType).forId(event != null ? event.getEventTypeId() : null)
        );
    }
    
    @Override
    public Event create(EventIn itemIn, boolean checkPermissions) {
        Event event = super.create(itemIn, checkPermissions);
        event.setResourceRequirements(event.getEventType().getResourceTypes().stream().map(resourceType -> {
            return new ResourceRequirement(event, resourceType);
        }).collect(Collectors.toList()));
        try {
            return repository.save(event);
        } catch (Exception ignore) {
            throw new ForbiddenException(ApiError.UNKNOWN_REASON);
        }
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
        if (rawIn == null || rawIn.has("eventTypeId")) {
            item.setEventType(eventTypeService.read(dto.getEventTypeId(), true));
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
        dto.setEventType(new EventTypeRefOut(item.getEventType()));
        dto.setResourceRequirements(resourceRequirementService.toOut(item.getResourceRequirements()));
        return dto;
    }

    public List<ResourceRequirement> getResourceRequirements(Long eventId) {
        return read(eventId, true).getResourceRequirements();
    }

    public List<ResourceRequirement> addResourceRequirement(Long eventId, Long resourceTypeId) {
        Event event = read(eventId, true);
        ResourceType resourceType = resourceTypeService.read(resourceTypeId, true);
        checkResourceRequirementPermission(event, resourceTypeId);

        ResourceRequirement resourceRequirement = new ResourceRequirement(event, resourceType);
        event.addResourceRequirement(resourceRequirement);
        try {
            return repository.save(event).getResourceRequirements();
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<ResourceRequirement> removeResourceRequirement(Long eventId, Long resourceRequirementId) {
        Event event = read(eventId, true);
        ResourceRequirement resourceRequirement = resourceRequirementService.read(resourceRequirementId);
        checkResourceRequirementPermission(event, resourceRequirement.getResourceType().getId());

        resourceRequirement.getResourceType().getResources();
        event.removeResourceRequirement(resourceRequirement);
        return repository.save(event).getResourceRequirements();
    }

    private void checkResourceRequirementPermission(Event event, Long resourceTypeId) {
        checkAnyPermission(
            permissionValue(PermissionType.EVENTS, PermissionAction.UPDATE).forPersistable(event),
            permissionValue(PermissionType.EVENTS_BY_EVENT_TYPES, PermissionAction.UPDATE).forId(event.getEventTypeId()),
            permissionValue(PermissionType.RESOURCE_TYPES, PermissionAction.ASSIGN).forId(resourceTypeId)
        );
    }

    private ResourceRequirement getResourceRequirement(Long eventId, Long resourceRequirementId) {
        ResourceRequirement rr = getResourceRequirements(eventId).stream().filter(item -> item.getId() == resourceRequirementId).findFirst().get();
        if (rr != null) {
            return rr;
        }
        throw new NotFoundException(ResourceRequirement.class, resourceRequirementId);
    }
    
    public List<Resource> getResources(Long eventId, Long resourceRequirementId) {
        return getResourceRequirement(eventId, resourceRequirementId).getResources();
    }

    public List<Resource> addResource(Long eventId, Long resourceRequirementId, Long resourceId) {
        Event event = read(eventId, true);
        ResourceRequirement resourceRequirement = getResourceRequirement(eventId, resourceRequirementId);
        checkResourceRequirementPermission(event, resourceRequirement.getResourceType().getId());

        if (resourceId != null) {
            Resource resource = resourceService.read(resourceId, true);
            if (!resource.getResourceTypes().contains(resourceRequirement.getResourceType())) {
                throw ForbiddenException.dontBelongsTo(Resource.class, resourceId, ResourceType.class, resourceRequirement.getResourceType().getId());
            }
            resourceRequirement.addResource(resource);
        } else {
           resourceRequirement.setResources(new ArrayList<>(resourceRequirement.getResourceType().getResources()));
        }
        try {
            return resourceRequirementRepository.save(resourceRequirement).getResources();
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<Resource> removeResource(Long eventId, Long resourceRequirementId, Long resourceId) {
        Event event = read(eventId, true);
        ResourceRequirement resourceRequirement = getResourceRequirement(eventId, resourceRequirementId);
        checkResourceRequirementPermission(event, resourceRequirement.getResourceType().getId());

        if (resourceId != null) {
            Resource resource = resourceService.read(resourceId, true);
            resourceRequirement.removeResource(resource);
        } else {
            resourceRequirement.setResources(null);
        }
        return resourceRequirementRepository.save(resourceRequirement).getResources();
    }

}
