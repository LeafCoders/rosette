package se.leafcoders.rosette.endpoint.article;

import java.util.List;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface ArticleRepository extends ModelRepository<Article> {
    
    public List<Article> findByArticleTypeId(Long articleTypeId);

    public List<Article> findByEventId(Long eventId);
}
