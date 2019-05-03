package se.leafcoders.rosette.controller.dto;

import java.util.ArrayList;
import java.util.List;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.service.AssetService;

public class ArticlesPublicOut extends ArrayList<ArticlePublicOut> {

    public ArticlesPublicOut(AssetService assetService, List<Article> articles) {
        articles.forEach(a -> add(new ArticlePublicOut(assetService, a)));
    }
}
