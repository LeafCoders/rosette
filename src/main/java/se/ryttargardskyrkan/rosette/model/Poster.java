package se.ryttargardskyrkan.rosette.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.ryttargardskyrkan.rosette.validator.HasIdRef;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
	private int duration;

	@HasIdRef(message = "poster.image.mustBeSet")
	private ObjectReference<UploadResponse> image;

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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

    public ObjectReference<UploadResponse> getImage() {
        return image;
    }

    public void setImage(ObjectReference<UploadResponse> image) {
        this.image = image;
    }
}
