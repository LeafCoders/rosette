package se.leafcoders.rosette.controller.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.service.AssetService;
import se.leafcoders.rosette.util.IdToSlugConverter;

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

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public List<ResourceRefPublicOut> getAuthors() {
        return authors;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public boolean getExpectingRecording() {
        return expectingRecording;
    }

    public String getArticleSerieSlug() {
        return articleSerieSlug;
    }

    public String getArticleSerieTitle() {
        return articleSerieTitle;
    }

}
