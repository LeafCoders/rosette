package se.leafcoders.rosette.endpoint.articletype;

import lombok.Data;

@Data
public class ArticleTypeRefOut {

    private Long id;
    private String articlesTitle;

    public ArticleTypeRefOut(ArticleType articleType) {
        id = articleType.getId();
        articlesTitle = articleType.getArticlesTitle();
    }
}
