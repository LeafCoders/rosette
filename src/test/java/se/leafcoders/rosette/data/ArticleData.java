package se.leafcoders.rosette.data;

import java.time.LocalDateTime;
import java.util.Collections;
import se.leafcoders.rosette.controller.dto.ArticleIn;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Event;
import se.leafcoders.rosette.persistence.model.Resource;

public class ArticleData {

    public static Article existingArticle(ArticleType articleType, ArticleSerie articleSerie, LocalDateTime time, String title, Resource author, Asset recording) {
        Article article = new Article();
        article.setArticleType(articleType);
        article.setArticleSerie(articleSerie);
        article.setTime(time);
        article.setAuthors(Collections.singletonList(author));
        article.setTitle(title);
        article.setContent("Innehåll...");
        article.setRecording(recording);
        return article;
    }

    public static ArticleIn missingAllProperties() {
        return new ArticleIn();
    }

    public static ArticleIn invalidProperties() {
        ArticleIn article = new ArticleIn();
        // TODO
        return article;
    }

    public static ArticleIn newArticle(Long articleTypeId, Long articleSerieId, LocalDateTime time, String title, Long authorId, Long recordingId) {
        ArticleIn article = new ArticleIn();
        article.setArticleTypeId(articleTypeId);
        article.setArticleSerieId(articleSerieId);
        article.setTime(time);
        article.setAuthorIds(Collections.singletonList(authorId));
        article.setTitle(title);
        article.setContent("Innehåll...");
        article.setRecordingId(recordingId);
        return article;
    }
    
    public static ArticleIn newArticleFromEvent(Long articleTypeId, Long articleSerieId, Event event, Long authorId, Long recordingId) {
        ArticleIn article = new ArticleIn();
        article.setArticleTypeId(articleTypeId);
        article.setArticleSerieId(articleSerieId);
        article.setEventId(event.getId());
        article.setTime(event.getStartTime());
        article.setAuthorIds(Collections.singletonList(authorId));
        article.setTitle(event.getTitle());
        article.setContent("Innehåll...");
        article.setRecordingId(recordingId);
        return article;
    }
    
}
