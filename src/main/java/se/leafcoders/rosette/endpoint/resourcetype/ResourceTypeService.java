package se.leafcoders.rosette.endpoint.resourcetype;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.resource.Resource;
import se.leafcoders.rosette.endpoint.resource.ResourceService;

@Service
public class ResourceTypeService extends PersistenceService<ResourceType, ResourceTypeIn, ResourceTypeOut> {

    @Autowired
    ResourceService resourceService;

    public ResourceTypeService(ResourceTypeRepository repository) {
        super(ResourceType.class, ResourceTypePermissionValue::new, repository);
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
            resourceType
                    .setDisplayOrder(Optional.ofNullable(repo().getHighestDisplayOrder()).map(i -> i + 1L).orElse(1L));
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
