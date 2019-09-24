package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

public class ArticleOut {

    private Long id;
    private Long articleTypeId;
    private String articleTypeIdAlias;
    private ArticleSerieRefOut articleSerie;
    private EventRefOut event;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    private List<ResourceRefOut> authors;
    private String title;
    private String contentRaw;
    private String contentHtml;
    private AssetOut recording;
    private String recordingStatus;

    
    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public String getArticleTypeIdAlias() {
        return articleTypeIdAlias;
    }

    public void setArticleTypeIdAlias(String articleTypeIdAlias) {
        this.articleTypeIdAlias = articleTypeIdAlias;
    }

    public ArticleSerieRefOut getArticleSerie() {
        return articleSerie;
    }

    public void setArticleSerie(ArticleSerieRefOut articleSerie) {
        this.articleSerie = articleSerie;
    }

    public EventRefOut getEvent() {
        return event;
    }

    public void setEvent(EventRefOut event) {
        this.event = event;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public List<ResourceRefOut> getAuthors() {
        return authors;
    }

    public void setAuthors(List<ResourceRefOut> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentRaw() {
        return contentRaw;
    }

    public void setContentRaw(String contentRaw) {
        this.contentRaw = contentRaw;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public AssetOut getRecording() {
        return recording;
    }

    public void setRecording(AssetOut recording) {
        this.recording = recording;
    }

    public String getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(String recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

}
