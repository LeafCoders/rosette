package se.leafcoders.rosette.endpoint.articleserie;

import lombok.Data;

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
