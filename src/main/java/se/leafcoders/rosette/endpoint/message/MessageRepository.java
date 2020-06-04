package se.leafcoders.rosette.endpoint.message;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface MessageRepository extends ModelRepository<Message> {
}
