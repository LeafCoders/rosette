package se.ryttargardskyrkan.rosette.model.event;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import se.ryttargardskyrkan.rosette.converter.RosetteDateJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateJsonSerializer;

public class EventDay {
	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date date;
	
	private Integer dayNumber;

	private List<Event> events;

	// Getters and setters

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(Integer dayNumber) {
		this.dayNumber = dayNumber;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
