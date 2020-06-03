package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.model.ResourceType;

@Repository
public interface ResourceTypeRepository extends ModelRepository<ResourceType> {

    @Query("SELECT r FROM ResourceType rt JOIN rt.resources r WHERE rt.id = :resourceTypeId ORDER BY r.lastUseTime desc")
    List<Resource> getResources(@Param("resourceTypeId") Long resourceTypeId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ResourceType r SET r.displayOrder = (r.displayOrder + :offset) WHERE r.displayOrder >= :fromDisplayOrder AND r.displayOrder <= :toDisplayOrder")
    int moveDisplayOrders(@Param("fromDisplayOrder") Long fromDisplayOrder, @Param("toDisplayOrder") Long toDisplayOrder, @Param("offset") Long offset);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ResourceType r SET r.displayOrder = :newDisplayOrder WHERE r.id = :resourceTypeId")
    int setDisplayOrder(@Param("resourceTypeId") Long resourceTypeId, @Param("newDisplayOrder") Long newDisplayOrder);

    @Query("SELECT MAX(r.displayOrder) FROM ResourceType r")
    Long getHighestDisplayOrder();
}
