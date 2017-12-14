package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.Article;

@Repository
public interface ArticleRepository extends ModelRepository<Article> {
    
    public List<Article> findByArticleTypeId(Long articleTypeId);
}
