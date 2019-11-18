package se.leafcoders.rosette.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.leafcoders.rosette.persistence.model.ArticleSerie;

@Repository
public interface ArticleSerieRepository extends ModelRepository<ArticleSerie> {
    
    public List<ArticleSerie> findByArticleTypeId(Long articleTypeId);

    @Transactional
    @Modifying
    @Query("UPDATE ArticleSerie ar_se SET ar_se.lastUseTime = :time WHERE ar_se.id = :id")
    int setLastUseTime(@Param("id") Long id, @Param("time") LocalDateTime time);
}
