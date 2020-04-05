package se.leafcoders.rosette.persistence.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.ResourceTypeIn;
import se.leafcoders.rosette.controller.dto.ResourceTypeOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.model.ResourceType;
import se.leafcoders.rosette.persistence.repository.ResourceTypeRepository;

@Service
public class ResourceTypeService extends PersistenceService<ResourceType, ResourceTypeIn, ResourceTypeOut> {

    @Autowired
    ResourceService resourceService;

    public ResourceTypeService(ResourceTypeRepository repository) {
        super(ResourceType.class, PermissionType::resourceTypes, repository);
    }
    
    private ResourceTypeRepository repo() {
        return (ResourceTypeRepository) repository;
    }

    @Override
    protected ResourceType convertFromInDTO(ResourceTypeIn dto, JsonNode rawIn, ResourceType item) {
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        return item;
    }

    @Override
    protected ResourceTypeOut convertToOutDTO(ResourceType item) {
        ResourceTypeOut dto = new ResourceTypeOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setDisplayOrder(item.getDisplayOrder());
        return dto;
    }

    public ResourceType create(ResourceTypeIn resourceTypeIn, boolean checkPermissions) {
        return super.create(resourceTypeIn, checkPermissions, (ResourceType resourceType) -> {
            resourceType.setDisplayOrder(Optional.ofNullable(repo().getHighestDisplayOrder()).map(i -> i + 1L).orElse(1L));
        });
    }

    public List<Resource> readResources(Long resourceTypeId) {
        return repo().getResources(resourceTypeId);
    }

    public void moveResourceType(Long resourceTypeId, Long toResourceTypeId) {
        final ResourceType resourceType = read(resourceTypeId, true);
        final ResourceType resourceTypeMoveTo = read(toResourceTypeId, true);
        if (resourceType.getDisplayOrder() < resourceTypeMoveTo.getDisplayOrder()) {
            repo().moveDisplayOrders(resourceType.getDisplayOrder(), resourceTypeMoveTo.getDisplayOrder(), -1L);
        } else {
            repo().moveDisplayOrders(resourceTypeMoveTo.getDisplayOrder(), resourceType.getDisplayOrder(), 1L);
        }
        repo().setDisplayOrder(resourceTypeId, resourceTypeMoveTo.getDisplayOrder());
    }
}
