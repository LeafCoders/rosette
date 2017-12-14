package se.leafcoders.rosette.persistence.repository;

import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Resource;

@Repository
public interface ResourceRepository extends ModelRepository<Resource> {
}
