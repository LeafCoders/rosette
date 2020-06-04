package se.leafcoders.rosette.endpoint.articleserie;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.HtmlContent;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.IdAlias;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.asset.Asset;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articleseries")
public class ArticleSerie extends Persistable {

    private static final long serialVersionUID = 2436164812683411105L;

    @NotNull(message = ApiString.NOT_NULL)
    @Column(name = "articletype_id", nullable = false, insertable = false, updatable = false)
    private Long articleTypeId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "articletype_id")
    private ArticleType articleType;

    @IdAlias
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String title;

    @Embedded
    private HtmlContent content;

    @JsonIgnore
    @Column(name = "image_id", nullable = false, insertable = false, updatable = false)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Asset image;

    private LocalDateTime lastUseTime;

    // Getters and setters

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
        this.setArticleTypeId(articleType != null ? articleType.getId() : null);
    }

    public void setImage(Asset image) {
        this.image = image;
        this.imageId = image != null ? image.getId() : null;
    }
}
