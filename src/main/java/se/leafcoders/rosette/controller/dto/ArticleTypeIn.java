package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.validator.IdAlias;
import se.leafcoders.rosette.persistence.validator.StringEnumeration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleTypeIn {

    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articlesTitle;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String newArticleTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articleSeriesTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String newArticleSerieTitle;

    @NotNull(message = ApiString.NOT_NULL)
    private Long imageFolderId;
    
    @NotNull(message = ApiString.NOT_NULL)
    private Long recordingFolderId;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @StringEnumeration(enumClass = ArticleType.RecordingStatus.class)
    private String defaultRecordingStatus;

    @NotNull(message = ApiString.NOT_NULL)
    private Long authorResourceTypeId;
    

    // Getters and setters

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getArticlesTitle() {
        return articlesTitle;
    }

    public void setArticlesTitle(String articlesTitle) {
        this.articlesTitle = articlesTitle;
    }

    public String getNewArticleTitle() {
        return newArticleTitle;
    }

    public void setNewArticleTitle(String newArticleTitle) {
        this.newArticleTitle = newArticleTitle;
    }

    public String getArticleSeriesTitle() {
        return articleSeriesTitle;
    }

    public void setArticleSeriesTitle(String articleSeriesTitle) {
        this.articleSeriesTitle = articleSeriesTitle;
    }

    public String getNewArticleSerieTitle() {
        return newArticleSerieTitle;
    }

    public void setNewArticleSerieTitle(String newArticleSerieTitle) {
        this.newArticleSerieTitle = newArticleSerieTitle;
    }

    public Long getImageFolderId() {
        return imageFolderId;
    }

    public void setImageFolderId(Long imageFolderId) {
        this.imageFolderId = imageFolderId;
    }

    public Long getRecordingFolderId() {
        return recordingFolderId;
    }

    public void setRecordingFolderId(Long recordingFolderId) {
        this.recordingFolderId = recordingFolderId;
    }

    public String getDefaultRecordingStatus() {
        return defaultRecordingStatus;
    }

    public void setDefaultRecordingStatus(String defaultRecordingStatus) {
        this.defaultRecordingStatus = defaultRecordingStatus;
    }

    public Long getAuthorResourceTypeId() {
        return authorResourceTypeId;
    }

    public void setAuthorResourceTypeId(Long authorResourceTypeId) {
        this.authorResourceTypeId = authorResourceTypeId;
    }
    
}
