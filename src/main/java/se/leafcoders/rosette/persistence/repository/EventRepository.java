package se.leafcoders.rosette.persistence.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Event;

@Repository
public interface EventRepository extends ModelRepository<Event> {

    @Query("SELECT e FROM Event e WHERE e.isPublic = 1 and e.eventTypeId IN (:eventTypeIds) and e.endTime >= :afterTime and e.startTime <= :beforeTime")
    List<Event> findForCalendar(
            @Param("eventTypeIds") Collection<Long> eventTypeIds,
            @Param("afterTime") LocalDateTime afterTime,
            @Param("beforeTime") LocalDateTime beforeTime);
}
