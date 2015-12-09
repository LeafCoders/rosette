package se.leafcoders.rosette.model.podcast;

import java.util.Date;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.converter.RosetteDateJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateJsonSerializer;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.education.EducationTypeRef;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "podcasts")
public class Podcast extends IdBasedModel {

    @HasRef(message = "podcast.educationType.mustBeSet")
    private EducationTypeRef educationType;

    // Attributes from http://www.apple.com/itunes/podcasts/specs.html

    @NotEmpty(message = "podcast.title.notEmpty")
    private String title;
    
    @NotEmpty(message = "podcast.subTitle.notEmpty")
    private String subTitle;
    
    @NotEmpty(message = "podcast.authorName.notEmpty")
    private String authorName;

    @NotEmpty(message = "podcast.copyright.notEmpty")
    private String copyright;

    @Length(max = 200, message = "podcast.description.max4000Chars")
    protected String description;

    @NotEmpty(message = "podcast.mainCategory.notEmpty")
    protected String mainCategory;

    @NotEmpty(message = "podcast.subCategory.notEmpty")
    protected String subCategory;
    
    @NotEmpty(message = "podcast.language.notEmpty")
    protected String language;
    
    @NotEmpty(message = "podcast.link.notEmpty")
    protected String link;

    @HasRef(message = "podcast.image.mustBeSet")
    private UploadResponse image;
    
	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date changedDate;

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
        Podcast podcastUpdate = (Podcast) updateFrom;
        
        if (rawData.has("educationType")) {
            setEducationType(podcastUpdate.getEducationType());
        }
        if (rawData.has("title")) {
            setTitle(podcastUpdate.getTitle());
        }
        if (rawData.has("subTitle")) {
            setSubTitle(podcastUpdate.getSubTitle());
        }
        if (rawData.has("authorName")) {
            setAuthorName(podcastUpdate.getAuthorName());
        }
        if (rawData.has("copyright")) {
            setCopyright(podcastUpdate.getCopyright());
        }
        if (rawData.has("description")) {
            setDescription(podcastUpdate.getDescription());
        }
        if (rawData.has("mainCategory")) {
            setMainCategory(podcastUpdate.getMainCategory());
        }
        if (rawData.has("subCategory")) {
            setSubCategory(podcastUpdate.getSubCategory());
        }
        if (rawData.has("language")) {
            setLanguage(podcastUpdate.getLanguage());
        }
        if (rawData.has("link")) {
            setLink(podcastUpdate.getLink());
        }
        if (rawData.has("image")) {
            setImage(podcastUpdate.getImage());
        }
        if (rawData.has("changedDate")) {
            setChangedDate(podcastUpdate.getChangedDate());
        }
    }

	// Getters and setters

    public EducationTypeRef getEducationType() {
        return educationType;
    }

    public void setEducationType(EducationTypeRef educationType) {
        this.educationType = educationType;
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

    public UploadResponse getImage() {
        return image;
    }

    public void setImage(UploadResponse image) {
        this.image = image;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

}
