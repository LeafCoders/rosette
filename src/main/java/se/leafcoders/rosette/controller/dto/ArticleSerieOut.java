package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

public class ArticleSerieOut {

    private Long id;
    private String idAlias;
    private Long articleTypeId;
    private String title;
    private String contentRaw;
    private String contentHtml;
    private AssetOut image;
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastUseTime;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public Long getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
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

    public AssetOut getImage() {
        return image;
    }

    public void setImage(AssetOut image) {
        this.image = image;
    }

    public LocalDateTime getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(LocalDateTime lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

}
