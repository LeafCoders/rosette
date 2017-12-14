package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Permission;

@Repository
public interface PermissionRepository extends ModelRepository<Permission> {

    public List<Permission> findByLevel(Integer level);

    // For level USER, GROUP, ... where entityId is applicable
    public List<Permission> findByLevelAndEntityId(Integer level, Long entityId);

    // For level USER, GROUP, ... where entityId is applicable
    public List<Permission> findByLevelAndEntityIdIn(Integer level, List<Long> entityIds);
}
