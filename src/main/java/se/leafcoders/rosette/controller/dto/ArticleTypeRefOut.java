package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.ArticleType;

public class ArticleTypeRefOut {

    private Long id;
    private String articlesTitle;

    public ArticleTypeRefOut(ArticleType articleType) {
        id = articleType.getId();
        articlesTitle = articleType.getArticlesTitle();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticlesTitle() {
        return articlesTitle;
    }

    public void setArticlesTitle(String articlesTitle) {
        this.articlesTitle = articlesTitle;
    }

}
