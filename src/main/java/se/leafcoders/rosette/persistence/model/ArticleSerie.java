package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "articleseries")
public class ArticleSerie extends Persistable {

    @NotNull(message = ApiString.NOT_NULL)
    @Column(name = "articletype_id", nullable = false, insertable = false, updatable = false)
    private Long articleTypeId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "articletype_id")
    private ArticleType articleType;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String title;

    @Length(max = 10000, message = ApiString.STRING_MAX_200_CHARS)
    private String content;

    @JsonIgnore
    @Column(name = "image_id", nullable = false, insertable = false, updatable = false)
    private Long imageId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Asset image;


    public ArticleSerie() {
    }

    // Getters and setters

    public Long getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
        this.setArticleTypeId(articleType != null ? articleType.getId() : null);
    }

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Asset getImage() {
        return image;
    }

    public void setImage(Asset image) {
        this.image = image;
        this.imageId = image != null ? image.getId() : null;
    }

}
