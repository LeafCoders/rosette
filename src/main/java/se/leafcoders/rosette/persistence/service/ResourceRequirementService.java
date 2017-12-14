package se.leafcoders.rosette.persistence.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import se.leafcoders.rosette.controller.dto.ResourceRefOut;
import se.leafcoders.rosette.controller.dto.ResourceRequirementOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeRefOut;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.persistence.model.ResourceRequirement;
import se.leafcoders.rosette.persistence.repository.ResourceRequirementRepository;

@Service
public class ResourceRequirementService {

    private ResourceRequirementRepository repository;
    
    public ResourceRequirementService(ResourceRequirementRepository repository) {
        this.repository = repository;
    }

    public ResourceRequirement read(Long id) {
        ResourceRequirement rr = repository.findOne(id);
        if (rr != null) {
            return rr;
        }
        throw new NotFoundException(ResourceRequirement.class.getSimpleName(), id);
    }
    
    public ResourceRequirementOut toOut(ResourceRequirement item) {
        return item != null ? convertToOutDTO(item) : null;
    }

    public List<ResourceRequirementOut> toOut(List<ResourceRequirement> items) {
        return items != null ? items.stream().map(item -> convertToOutDTO(item)).collect(Collectors.toList()) : null;
    }

    protected ResourceRequirementOut convertToOutDTO(ResourceRequirement item) {
        ResourceRequirementOut dto = new ResourceRequirementOut();
        dto.setId(item.getId());
        dto.setResourceType(new ResourceTypeRefOut(item.getResourceType()));
        dto.setResources(item.getResources().stream().map(resource -> new ResourceRefOut(resource)).collect(Collectors.toList()));
        return dto;
    }

}
