package se.ryttargardskyrkan.rosette.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;

@Document(collection = "events")
public class Event {
	@Id
	private String id;

	@NotNull
	private String title;

	@NotNull
	@Indexed
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date startTime;

	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date endTime;
	
	@Indexed
	private String themeId;

	// Getters and setters

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}
}
