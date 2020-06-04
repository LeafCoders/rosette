package se.leafcoders.rosette.endpoint.article;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.HtmlContent;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerie;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.articletype.ArticleType.RecordingStatus;
import se.leafcoders.rosette.endpoint.asset.Asset;
import se.leafcoders.rosette.endpoint.event.Event;
import se.leafcoders.rosette.endpoint.resource.Resource;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article extends Persistable {

    private static final long serialVersionUID = -8107708146887268894L;

    @NotNull(message = ApiString.NOT_NULL)
    @Column(name = "articletype_id", nullable = false, insertable = false, updatable = false)
    private Long articleTypeId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "articletype_id")
    private ArticleType articleType;

    @Column(name = "articleserie_id", nullable = false, insertable = false, updatable = false)
    private Long articleSerieId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "articleserie_id")
    private ArticleSerie articleSerie;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastModifiedTime;
    
    // Any time you want it to be...
    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_authors", joinColumns = { @JoinColumn(name = "article_id") }, inverseJoinColumns = { @JoinColumn(name = "authors_id") },
        uniqueConstraints = { @UniqueConstraint(columnNames = { "article_id", "authors_id" }) }
    )
    private List<Resource> authors = new ArrayList<>();
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String title;

    @Embedded
    private HtmlContent content;

    @ManyToOne
    @JoinColumn(name = "recording_id")
    private Asset recording;

    @NotNull(message = ApiString.STRING_NOT_EMPTY)
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RecordingStatus recordingStatus;

    // Getters and setters

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
        this.setArticleTypeId(articleType != null ? articleType.getId() : null);
    }

    public void setArticleSerie(ArticleSerie articleSerie) {
        this.articleSerie = articleSerie;
        this.setArticleSerieId(articleSerie != null ? articleSerie.getId() : null);
    }

    public List<Resource> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        authors.sort((a, b) -> a.getName().compareTo(b.getName()));
        return authors;
    }

    public HtmlContent getContent() {
        return content != null ? content : new HtmlContent();
    }

    public void addAuthor(Resource authors) {
        getAuthors().add(authors);
    }

    public void removeAuthor(Resource authors) {
        getAuthors().remove(authors);
    }
}
