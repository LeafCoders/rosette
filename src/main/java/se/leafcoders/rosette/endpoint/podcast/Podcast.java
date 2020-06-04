package se.leafcoders.rosette.endpoint.podcast;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.IdAlias;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.asset.Asset;

// Attributes from http://www.apple.com/itunes/podcasts/specs.html

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "podcasts")
public class Podcast extends Persistable {

    private static final long serialVersionUID = 815066400713334765L;

    @JsonIgnore
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
    private String title;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String subTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String authorName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Email(message = ApiString.EMAIL_INVALID)
    private String authorEmail;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String copyright;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String description;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String mainCategory;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String subCategory;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String language;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String authorLink;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articlesLink;

    @JsonIgnore
    @Column(name = "image_id", nullable = false, insertable = false, updatable = false)
    private Long imageId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Asset image;
    
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime changedDate;

    // Getters and setters

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
        this.articleTypeId = articleType != null ? articleType.getId() : null;
    }

    public void setImage(Asset image) {
        this.image = image;
        this.imageId = image != null ? image.getId() : null;
    }
}
