package se.leafcoders.rosette.persistence.repository;

import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Podcast;

@Repository
public interface PodcastRepository extends ModelRepository<Podcast> {
}
