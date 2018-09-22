package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.model.ResourceType;

@Repository
public interface ResourceTypeRepository extends ModelRepository<ResourceType> {

    @Query("SELECT r FROM ResourceType rt JOIN rt.resources r WHERE rt.id = :resourceTypeId ORDER BY r.lastUseTime desc")
    List<Resource> getResources(@Param("resourceTypeId") Long resourceTypeId);
}
