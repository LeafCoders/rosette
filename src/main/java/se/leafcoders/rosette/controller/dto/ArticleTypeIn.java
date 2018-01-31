package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleTypeIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String articlesTitle;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String newArticleTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String articleSeriesTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String newArticleSerieTitle;

    @NotNull(message = ApiString.NOT_NULL)
    private Long assetFolderId;
    

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

    public Long getAssetFolderId() {
        return assetFolderId;
    }

    public void setAssetFolderId(Long assetFolderId) {
        this.assetFolderId = assetFolderId;
    }
    
}
