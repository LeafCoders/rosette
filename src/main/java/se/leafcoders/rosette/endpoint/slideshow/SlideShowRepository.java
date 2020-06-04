package se.leafcoders.rosette.endpoint.slideshow;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface SlideShowRepository extends ModelRepository<SlideShow> {
    
    public SlideShow findOneByIdAlias(String idAlias);
}
