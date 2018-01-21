package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

@Entity
@Table(name = "articles")
public class Article extends Persistable {

    @NotNull(message = ApiString.NOT_NULL)
    @Column(name = "articletype_id", nullable = false, updatable = false)
    private Long articleTypeId;
    
    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastModifiedTime;
    
    // Any time you want it to be...
    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    @Column(name = "articleserie_id", nullable = false, insertable = false, updatable = false)
    private Long articleSerieId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "articleserie_id")
    private ArticleSerie articleSerie;

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

    @Length(max = 10000, message = ApiString.STRING_MAX_200_CHARS)
    private String content;


    public Article() {
    }

    // Getters and setters

    public Long getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Long getArticleSerieId() {
        return articleSerieId;
    }

    public void setArticleSerieId(Long articleSerieId) {
        this.articleSerieId = articleSerieId;
    }

    public ArticleSerie getArticleSerie() {
        return articleSerie;
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

    public void setAuthors(List<Resource> authors) {
        this.authors = authors;
    }

    public void addAuthor(Resource authors) {
        getAuthors().add(authors);
    }

    public void removeAuthor(Resource authors) {
        getAuthors().remove(authors);
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

}