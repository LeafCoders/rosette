package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.ArticleType;

@Data
public class ArticleTypeRefOut {

    private Long id;
    private String articlesTitle;

    public ArticleTypeRefOut(ArticleType articleType) {
        id = articleType.getId();
        articlesTitle = articleType.getArticlesTitle();
    }
}
