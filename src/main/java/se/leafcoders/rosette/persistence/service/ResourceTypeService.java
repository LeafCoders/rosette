package se.leafcoders.rosette.persistence.service;

import java.util.List;
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
        super(ResourceType.class, PermissionType.RESOURCE_TYPES, repository);
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
        return dto;
    }
    
    public List<Resource> readResources(Long resourceTypeId) {
        return read(resourceTypeId, true).getResources();
    }
}
