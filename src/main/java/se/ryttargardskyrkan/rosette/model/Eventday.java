package se.ryttargardskyrkan.rosette.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import se.ryttargardskyrkan.rosette.converter.RosetteJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteJsonSerializer;

public class Eventday {
	@JsonSerialize(using = RosetteJsonSerializer.class)
	@JsonDeserialize(using = RosetteJsonDeserializer.class)
	private Date date;

	private List<Event> events;

	// Getters and setters

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
