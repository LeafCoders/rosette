package se.leafcoders.rosette.persistence.repository;

import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Event;

@Repository
public interface EventRepository extends ModelRepository<Event> {
    
}
