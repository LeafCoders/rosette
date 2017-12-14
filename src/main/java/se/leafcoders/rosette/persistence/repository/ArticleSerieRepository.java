package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.ArticleSerie;

@Repository
public interface ArticleSerieRepository extends ModelRepository<ArticleSerie> {
    
    public List<ArticleSerie> findByArticleTypeId(Long articleTypeId);
}
