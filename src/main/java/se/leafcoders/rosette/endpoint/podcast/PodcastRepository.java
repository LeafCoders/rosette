package se.leafcoders.rosette.endpoint.podcast;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface PodcastRepository extends ModelRepository<Podcast> {

    public Podcast findOneByIdAlias(String idAlias);
}
