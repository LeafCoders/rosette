package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PodcastIn {

    @NotNull(message = ApiString.NOT_NULL)
    private Long articleTypeId;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String title;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String subTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String authorName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String copyright;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_4000_CHARS)
    protected String description;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    protected String mainCategory;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    protected String subCategory;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    protected String language;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    protected String link;

    @NotNull(message = ApiString.NOT_NULL)
    private Long imageId;

    // Getters and setters

    public Long getArticleTypeId() {
        return articleTypeId;
    }
    
    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
}
