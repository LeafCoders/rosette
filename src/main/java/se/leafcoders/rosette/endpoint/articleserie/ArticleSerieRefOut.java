package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.ArticleSerie;

@Data
public class ArticleSerieRefOut {

    private Long id;
    private Long articleTypeId;
    private String title;

    public ArticleSerieRefOut(ArticleSerie articleSerie) {
        id = articleSerie.getId();
        articleTypeId = articleSerie.getArticleTypeId();
        title = articleSerie.getTitle();
    }
}
