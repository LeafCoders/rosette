package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.persistence.validator.IdAlias;

// Attributes from http://www.apple.com/itunes/podcasts/specs.html

@Entity
@Table(name = "podcasts")
public class Podcast extends Persistable {

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


    public Podcast() {
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
        this.articleTypeId = articleType != null ? articleType.getId() : null;
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthorLink() {
        return authorLink;
    }

    public void setAuthorLink(String authorLink) {
        this.authorLink = authorLink;
    }

    public String getArticlesLink() {
        return articlesLink;
    }

    public void setArticlesLink(String articlesLink) {
        this.articlesLink = articlesLink;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Asset getImage() {
        return image;
    }

    public void setImage(Asset image) {
        this.image = image;
        this.imageId = image != null ? image.getId() : null;
    }

    public LocalDateTime getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(LocalDateTime changedDate) {
        this.changedDate = changedDate;
    }
}
