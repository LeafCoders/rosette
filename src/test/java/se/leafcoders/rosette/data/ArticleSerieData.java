package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.ArticleSerieIn;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.model.Asset;

public class ArticleSerieData {

    public static ArticleSerie existingArticleSerie(ArticleType articleType, String idAlias, Asset image) {
        ArticleSerie articleSerie = new ArticleSerie();
        articleSerie.setArticleType(articleType);
        articleSerie.setIdAlias(idAlias);
        articleSerie.setTitle("En serie " + idAlias);
        articleSerie.setContent("Innehåll...");
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

    public static ArticleSerieIn newArticleSerie(Long articleTypeId, String idAlias, Long imageId) {
        ArticleSerieIn articleSerie = new ArticleSerieIn();
        articleSerie.setArticleTypeId(articleTypeId);
        articleSerie.setIdAlias(idAlias);
        articleSerie.setTitle("En serie " + idAlias);
        articleSerie.setContent("Innehåll...");
        articleSerie.setImageId(imageId);
        return articleSerie;
    }
    
}
