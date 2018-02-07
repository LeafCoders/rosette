package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "articletypes")
public class ArticleType extends Persistable {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    @Column(nullable = false, unique = true)
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
    @ManyToOne
    @JoinColumn(name = "imagefolder_id")
    private AssetFolder imageFolder;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "recordingfolder_id")
    private AssetFolder recordingFolder;
    
    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "author_resourcetype_id")
    private ResourceType authorResourceType;


    public ArticleType() {
    }

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

    public AssetFolder getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(AssetFolder imageFolder) {
        this.imageFolder = imageFolder;
    }

    public AssetFolder getRecordingFolder() {
        return recordingFolder;
    }

    public void setRecordingFolder(AssetFolder recordingFolder) {
        this.recordingFolder = recordingFolder;
    }

    public ResourceType getAuthorResourceType() {
        return authorResourceType;
    }

    public void setAuthorResourceType(ResourceType authorResourceType) {
        this.authorResourceType = authorResourceType;
    }

}
