package se.leafcoders.rosette.endpoint.article;

import java.util.ArrayList;
import java.util.List;

import se.leafcoders.rosette.endpoint.asset.AssetService;

public class ArticlesPublicOut extends ArrayList<ArticlePublicOut> {

    private static final long serialVersionUID = -5419163825340205153L;

    public ArticlesPublicOut(AssetService assetService, List<Article> articles) {
        articles.forEach(a -> add(new ArticlePublicOut(assetService, a)));
    }
}
