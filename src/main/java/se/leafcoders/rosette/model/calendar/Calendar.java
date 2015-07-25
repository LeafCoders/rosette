package se.leafcoders.rosette.model.calendar;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import se.leafcoders.rosette.converter.RosetteDateJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Calendar {

	private Integer year;
	private Integer week;

	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date fromDate;

	@JsonSerialize(using = RosetteDateJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateJsonDeserializer.class)
	private Date untilDate;

    private List<CalendarDay> days = new LinkedList<CalendarDay>();

    public void addDay(CalendarDay day) {
    	days.add(day);
    }
    
	// Getters and setters

    public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getUntilDate() {
		return untilDate;
	}

	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}

	public List<CalendarDay> getDays() {
		return days;
	}

	public void setDays(List<CalendarDay> days) {
		this.days = days;
	}
}
