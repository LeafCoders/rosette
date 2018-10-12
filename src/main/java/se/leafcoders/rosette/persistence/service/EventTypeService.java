package se.leafcoders.rosette.persistence.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.EventTypeIn;
import se.leafcoders.rosette.controller.dto.EventTypeOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeRefOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.EventType;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.EventTypeRepository;

@Service
public class EventTypeService extends PersistenceService<EventType, EventTypeIn, EventTypeOut> {

    @Autowired
    ResourceTypeService resourceTypeService;

    private class ResourceTypeCrud extends ConnectedCrud<EventType, ResourceType> {

        public ResourceTypeCrud() {
            super(EventTypeService.this, resourceTypeService, repository);
        }

        @Override
        public void addChild(EventType parent, ResourceType child) {
            parent.addResourceType(child);
        }

        @Override
        public void removeChild(EventType parent, ResourceType child) {
            parent.removeResourceType(child);
        }

        @Override
        public List<ResourceType> readChildren(EventType parent) {
            return parent.getResourceTypes();
        }
    };

    public EventTypeService(EventTypeRepository repository) {
        super(EventType.class, PermissionType::eventTypes, repository);
    }

    @Override
    protected EventType convertFromInDTO(EventTypeIn dto, JsonNode rawIn, EventType item) {
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        if (rawIn == null || rawIn.has("isPublic")) {
            item.setIsPublic(dto.getIsPublic());
        }
        return item;
    }

    @Override
    protected EventTypeOut convertToOutDTO(EventType item) {
        EventTypeOut dto = new EventTypeOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setIsPublic(item.getIsPublic());
        dto.setResourceTypes(item.getResourceTypes().stream().map(resourceType -> new ResourceTypeRefOut(resourceType)).collect(Collectors.toList()));
        return dto;
    }

    public List<ResourceType> getResourceTypes(Long eventTypeId) {
        return new ResourceTypeCrud().readAll(eventTypeId);
    }

    public List<ResourceType> addResourceType(Long eventTypeId, Long resourceTypeId) {
        return new ResourceTypeCrud().connect(eventTypeId, resourceTypeId);
    }

    public List<ResourceType> removeResourceType(Long eventTypeId, Long resourceTypeId) {
        return new ResourceTypeCrud().disconnect(eventTypeId, resourceTypeId);
    }
}
