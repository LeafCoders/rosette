package se.leafcoders.rosette.endpoint.permission;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface PermissionRepository extends ModelRepository<Permission> {

    public List<Permission> findByLevel(Integer level);

    // For level USER, GROUP, ... where entityId is applicable
    public List<Permission> findByLevelAndEntityId(Integer level, Long entityId);

    // For level USER, GROUP, ... where entityId is applicable
    public List<Permission> findByLevelAndEntityIdIn(Integer level, List<Long> entityIds);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p INNER JOIN p.permissionSets ps WHERE ps.id = :permissionSetId and p.id = :permissionId")
    boolean isPermissionSetInPermission(@Param("permissionSetId") Long permissionSetId,
            @Param("permissionId") Long permissionId);
}
