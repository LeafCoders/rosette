package se.leafcoders.rosette.persistence.service;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.controller.dto.ResourceIn;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeRefOut;
import se.leafcoders.rosette.controller.dto.UserRefOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.converter.ClientServerTime;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.ResourceRepository;

@Service
public class ResourceService extends PersistenceService<Resource, ResourceIn, ResourceOut> {

    @Autowired
    UserService userService;

    @Autowired
    ResourceTypeService resourceTypeService;

    public ResourceService(ResourceRepository repository) {
        super(Resource.class, PermissionType::resources, repository);
    }

    private ResourceRepository repo() {
        return (ResourceRepository) repository;
    }

    @Override
    protected Resource convertFromInDTO(ResourceIn dto, JsonNode rawIn, Resource item) {
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        if (rawIn == null || rawIn.has("userId")) {
            item.setUser(userService.read(dto.getUserId(), true));
        }
        return item;
    }

    @Override
    protected ResourceOut convertToOutDTO(Resource item) {
        ResourceOut dto = new ResourceOut();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setResourceTypes(item.getResourceTypes().stream().map(resourceType -> new ResourceTypeRefOut(resourceType)).collect(Collectors.toList()));
        dto.setUser(item.getUser() != null ? new UserRefOut(item.getUser()) : null);
        dto.setLastUseTime(item.getLastUseTime());
        return dto;
    }

    public List<ResourceType> getResourceTypes(Long resourceId) {
        return read(resourceId, true).getResourceTypes();
    }

    public List<ResourceType> addResourceType(Long resourceId, Long resourceTypeId) {
        checkPermission(PermissionType.resources().update().forId(resourceId));
        Resource resource = read(resourceId, true);
        ResourceType resourceType = resourceTypeService.read(resourceTypeId, true);
        resource.addResourceType(resourceType);
        try {
            return repository.save(resource).getResourceTypes();
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<ResourceType> removeResourceType(Long resourceId, Long resourceTypeId) {
        checkPermission(PermissionType.resources().update().forId(resourceId));
        Resource resource = read(resourceId, true);
        ResourceType resourceType = resourceTypeService.read(resourceTypeId, true);
        resource.removeResourceType(resourceType);
        return repository.save(resource).getResourceTypes();
    }
    
    public void updateUsage(Resource resource) {
        try {
            repo().setLastUseTime(resource.getId(), ClientServerTime.serverTimeNow());
        } catch (Exception ignore) {}
    }
}
