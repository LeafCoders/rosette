package se.leafcoders.rosette.model;

import java.util.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.validator.HasRef;
import se.leafcoders.rosette.validator.CheckReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "posters")
@ScriptAssert(lang = "javascript", script = "_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime)", message = "poster.startBeforeEndTime")
public class Poster extends IdBasedModel {

	@NotEmpty(message = "poster.title.notEmpty")
	private String title;

	// Start using poster after this time
	@Indexed
	@NotNull(message = "poster.startTime.notNull")
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date startTime;

	// Don't use poster after this time
	@Indexed
	@NotNull(message = "poster.endTime.notNull")
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date endTime;

	// Number of seconds to display poster in "slide show"
	@Min(value = 1, message = "poster.duration.tooShort")
	private Integer duration;

	@HasRef(message = "poster.image.mustBeSet")
    @CheckReference
	private UploadResponse image;

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		Poster posterUpdate = (Poster) updateFrom;
		if (rawData.has("title")) {
			setTitle(posterUpdate.getTitle());
		}
		if (rawData.has("startTime")) {
			setStartTime(posterUpdate.getStartTime());
		}
		if (rawData.has("endTime")) {
			setEndTime(posterUpdate.getEndTime());
		}
		if (rawData.has("duration")) {
			setDuration(posterUpdate.getDuration());
		}
		if (rawData.has("image")) {
			setImage(posterUpdate.getImage());
		}
	}
	
	// Getters and setters

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

    public UploadResponse getImage() {
        return image;
    }

    public void setImage(UploadResponse image) {
        this.image = image;
    }
}
