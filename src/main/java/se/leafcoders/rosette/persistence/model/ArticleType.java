package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

@Entity
@Table(name = "articletypes")
public class ArticleType extends Persistable {

    public enum RecordingStatus { NOT_EXPECTED, EXPECTING_RECORDING, HAS_RECORDING };
    
    @IdAlias
    @Column(nullable = false, unique = true)
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
    @ManyToOne
    @JoinColumn(name = "imagefolder_id")
    private AssetFolder imageFolder;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "recordingfolder_id")
    private AssetFolder recordingFolder;

    @NotNull(message = ApiString.STRING_NOT_EMPTY)
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RecordingStatus defaultRecordingStatus;

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

    public RecordingStatus getDefaultRecordingStatus() {
        return defaultRecordingStatus;
    }

    public void setDefaultRecordingStatus(RecordingStatus defaultRecordingStatus) {
        this.defaultRecordingStatus = defaultRecordingStatus;
    }

    public ResourceType getAuthorResourceType() {
        return authorResourceType;
    }

    public void setAuthorResourceType(ResourceType authorResourceType) {
        this.authorResourceType = authorResourceType;
    }

}
