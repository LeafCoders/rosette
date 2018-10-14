package se.leafcoders.rosette.persistence.service;

import java.util.Set;
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
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ResourceRequirement.class.getSimpleName(), id));
    }
    
    public ResourceRequirementOut toOut(ResourceRequirement item) {
        return item != null ? convertToOutDTO(item) : null;
    }

    public Set<ResourceRequirementOut> toOut(Set<ResourceRequirement> items) {
        return items != null ? items.stream().map(item -> convertToOutDTO(item)).collect(Collectors.toSet()) : null;
    }

    protected ResourceRequirementOut convertToOutDTO(ResourceRequirement item) {
        ResourceRequirementOut dto = new ResourceRequirementOut();
        dto.setId(item.getId());
        dto.setResourceType(new ResourceTypeRefOut(item.getResourceType()));
        dto.setResources(item.getResources().stream().map(resource -> new ResourceRefOut(resource)).collect(Collectors.toSet()));
        return dto;
    }

}
