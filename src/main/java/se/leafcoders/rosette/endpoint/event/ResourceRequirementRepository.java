package se.leafcoders.rosette.endpoint.event;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface ResourceRequirementRepository extends ModelRepository<ResourceRequirement> {
}
