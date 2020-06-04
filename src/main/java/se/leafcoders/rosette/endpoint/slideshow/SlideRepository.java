package se.leafcoders.rosette.endpoint.slideshow;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface SlideRepository extends ModelRepository<Slide> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Slide s SET s.displayOrder = (s.displayOrder + :offset) WHERE s.slideShowId = :slideShowId AND s.displayOrder >= :fromDisplayOrder AND s.displayOrder <= :toDisplayOrder")
    int moveDisplayOrders(@Param("slideShowId") Long slideShowId, @Param("fromDisplayOrder") Long fromDisplayOrder, @Param("toDisplayOrder") Long toDisplayOrder, @Param("offset") Long offset);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Slide s SET s.displayOrder = :newDisplayOrder WHERE s.id = :slideId")
    int setDisplayOrder(@Param("slideId") Long slideId, @Param("newDisplayOrder") Long newDisplayOrder);

    @Query("SELECT MAX(s.displayOrder) FROM Slide s WHERE s.slideShowId = :slideShowId")
    Long getHighestDisplayOrder(@Param("slideShowId") Long slideShowId);
}
