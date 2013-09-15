package se.ryttargardskyrkan.rosette.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import se.ryttargardskyrkan.rosette.converter.RosetteDateJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateJsonSerializer;

public class Eventweek {
	private Integer week;
	
	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date since;
	
	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date until;
	
	private List<Eventday> days;

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

	public List<Eventday> getDays() {
		return days;
	}

	public void setDays(List<Eventday> days) {
		this.days = days;
	}
}
