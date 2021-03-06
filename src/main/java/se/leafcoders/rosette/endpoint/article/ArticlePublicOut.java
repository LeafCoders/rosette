package se.leafcoders.rosette.endpoint.article;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Data;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.asset.AssetService;
import se.leafcoders.rosette.endpoint.resource.ResourceRefPublicOut;
import se.leafcoders.rosette.util.IdToSlugConverter;

@Data
public class ArticlePublicOut {

    private static final String ARTICLE_SLUG_PREFIX = "ar";
    private static final String ARTICLE_SERIE_SLUG_PREFIX = "as";
    
    private String slug;
    private String title;
    private String content;
    private String time;
    private List<ResourceRefPublicOut> authors;
    private String imageUrl;
    private String recordingUrl;
    private boolean expectingRecording;
    private String articleSerieSlug;
    private String articleSerieTitle;

    public static String slug(final Article article) {
        return IdToSlugConverter.convertIdToSlug(article.getId(), article.getTitle(), ARTICLE_SLUG_PREFIX);
    }
    
    public ArticlePublicOut(AssetService assetService, Article article) {
        slug = slug(article);
        title = article.getTitle();
        content = article.getContent().getContentHtml();
        if (article.getTime() != null) {
            time = article.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
        }
        authors = article.getAuthors().stream().map(author -> new ResourceRefPublicOut(author)).collect(Collectors.toList());
        imageUrl = assetService.urlOfAsset(article.getArticleSerie().getImage());
        recordingUrl = Optional.ofNullable(article.getRecording()).map(assetService::urlOfAsset).orElse(null);
        expectingRecording = ArticleType.RecordingStatus.EXPECTING_RECORDING.equals(article.getRecordingStatus());
        if (article.getArticleSerie() != null) {
            articleSerieSlug = IdToSlugConverter.convertIdToSlug(article.getArticleSerie().getId(), article.getArticleSerie().getTitle(), ARTICLE_SERIE_SLUG_PREFIX);
            articleSerieTitle = article.getArticleSerie().getTitle();
        }
    }
}
