package se.leafcoders.rosette.model.calendar;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import se.leafcoders.rosette.converter.RosetteDateJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class CalendarDay {

	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date date;
	
	private Integer weekDay;
	
	private List<CalendarEvent> events = new LinkedList<CalendarEvent>();

	public void addEvent(CalendarEvent event) {
		events.add(event);
	}
	
	// Getters and setters

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(Integer weekDay) {
		this.weekDay = weekDay;
	}

	public List<CalendarEvent> getEvents() {
		return events;
	}

	public void setEvents(List<CalendarEvent> events) {
		this.events = events;
	}
}
