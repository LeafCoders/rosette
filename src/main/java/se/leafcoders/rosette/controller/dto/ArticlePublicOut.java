package se.leafcoders.rosette.controller.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.service.AssetService;

public class ArticlePublicOut {
    private Long id;
    private String title;
    private String content;
    private String time;
    private List<ResourceRefOut> authors;
    private String imageUrl;
    private String recordingUrl;
    private Long articleSerieId;
    private String articleSerieTitle;
    
    public ArticlePublicOut(AssetService assetService, Article article) {
        id = article.getId();
        title = article.getTitle();
        content = article.getContent().getContentHtml();
        if (article.getTime() != null) {
            time = article.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
        }
        authors = article.getAuthors().stream().map(author -> new ResourceRefOut(author)).collect(Collectors.toList());
        imageUrl = assetService.urlOfAsset(article.getArticleSerie().getImage());
        recordingUrl = assetService.urlOfAsset(article.getRecording());
        if (article.getArticleSerie() != null) {
            articleSerieId = article.getArticleSerie().getId();
            articleSerieTitle = article.getArticleSerie().getTitle();
        }
    }
    
    public Long getId() {
        return id;
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

    public List<ResourceRefOut> getAuthors() {
        return authors;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getRecordingUrl() {
        return recordingUrl;
    }
    
    public Long getArticleSerieId() {
        return articleSerieId;
    }

    public String getArticleSerieTitle() {
        return articleSerieTitle;
    }
}
