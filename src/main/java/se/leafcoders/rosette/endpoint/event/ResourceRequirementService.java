package se.leafcoders.rosette.endpoint.event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.exception.NotFoundException;
import se.leafcoders.rosette.endpoint.resource.ResourceRefOut;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRefOut;

@Service
public class ResourceRequirementService {

    private ResourceRequirementRepository repository;

    public ResourceRequirementService(ResourceRequirementRepository repository) {
        this.repository = repository;
    }

    public ResourceRequirement read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ResourceRequirement.class.getSimpleName(), id));
    }

    public ResourceRequirementOut toOut(ResourceRequirement item) {
        return item != null ? convertToOutDTO(item) : null;
    }

    public List<ResourceRequirementOut> toOut(Set<ResourceRequirement> items) {
        return items != null ? items.stream().map(item -> convertToOutDTO(item))
                .sorted(ResourceRequirementOutComparator.comparator())
                .collect(Collectors.toList()) : null;
    }

    protected ResourceRequirementOut convertToOutDTO(ResourceRequirement item) {
        ResourceRequirementOut dto = new ResourceRequirementOut();
        dto.setId(item.getId());
        dto.setResourceType(new ResourceTypeRefOut(item.getResourceType()));
        dto.setResources(
                item.getResources().stream().map(resource -> new ResourceRefOut(resource)).collect(Collectors.toSet()));
        return dto;
    }

}
