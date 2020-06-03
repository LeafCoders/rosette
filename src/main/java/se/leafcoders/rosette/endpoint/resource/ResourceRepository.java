package se.leafcoders.rosette.persistence.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.leafcoders.rosette.persistence.model.Resource;

@Repository
public interface ResourceRepository extends ModelRepository<Resource> {

    @Transactional
    @Modifying
    @Query("UPDATE Resource r SET r.lastUseTime = :time WHERE r.id = :id")
    int setLastUseTime(@Param("id") Long id, @Param("time") LocalDateTime time);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Resource r INNER JOIN r.resourceTypes rt WHERE rt.id = :resourceTypeId and r.id = :resourceId")
    boolean isResourceTypeInResource(@Param("resourceTypeId") Long resourceTypeId,
            @Param("resourceId") Long resourceId);
}
