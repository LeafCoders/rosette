package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.ArticleSerie;

public class ArticleSerieRefOut {

    private Long id;
    private Long articleTypeId;
    private String title;

    // Getters and setters

    public ArticleSerieRefOut(ArticleSerie articleSerie) {
        id = articleSerie.getId();
        articleTypeId = articleSerie.getArticleTypeId();
        title = articleSerie.getTitle();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
