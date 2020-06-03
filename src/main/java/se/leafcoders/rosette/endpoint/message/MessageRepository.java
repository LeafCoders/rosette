package se.leafcoders.rosette.persistence.repository;

import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Message;

@Repository
public interface MessageRepository extends ModelRepository<Message> {
}
