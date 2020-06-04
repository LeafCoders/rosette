package se.leafcoders.rosette.endpoint.articleserie;

import se.leafcoders.rosette.core.persistable.HtmlContent;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.asset.Asset;

public class ArticleSerieData {

    public static ArticleSerie existingArticleSerie(ArticleType articleType, String idAlias, Asset image) {
        ArticleSerie articleSerie = new ArticleSerie();
        articleSerie.setArticleType(articleType);
        articleSerie.setIdAlias(idAlias);
        articleSerie.setTitle("En serie " + idAlias);
        articleSerie.setContent(new HtmlContent("Innehåll...", "Innehåll..."));
        articleSerie.setImage(image);
        return articleSerie;
    }

    public static ArticleSerieIn missingAllProperties() {
        return new ArticleSerieIn();
    }

    public static ArticleSerieIn invalidProperties() {
        ArticleSerieIn articleSerie = new ArticleSerieIn();
        // TODO
        return articleSerie;
    }

    public static ArticleSerieIn newArticleSerie(Long articleTypeId, String idAlias, String title, Long imageId) {
        ArticleSerieIn articleSerie = new ArticleSerieIn();
        articleSerie.setArticleTypeId(articleTypeId);
        articleSerie.setIdAlias(idAlias);
        articleSerie.setTitle("En serie " + title);
        articleSerie.setContentRaw("Innehåll...");
        articleSerie.setContentHtml("Innehåll...");
        articleSerie.setImageId(imageId);
        return articleSerie;
    }
    
}
