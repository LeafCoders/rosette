package se.leafcoders.rosette.model.event;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import se.leafcoders.rosette.converter.RosetteDateJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateJsonSerializer;

public class EventWeek {
	private Integer week;
	
	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date since;
	
	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date until;
	
	private List<EventDay> days;

	// Getters and setters

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Date getSince() {
		return since;
	}

	public void setSince(Date since) {
		this.since = since;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public List<EventDay> getDays() {
		return days;
	}

	public void setDays(List<EventDay> days) {
		this.days = days;
	}
}
