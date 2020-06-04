package se.leafcoders.rosette.endpoint.permissionset;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface PermissionSetRepository extends ModelRepository<PermissionSet> {
}
