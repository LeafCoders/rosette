package se.ryttargardskyrkan.rosette.model;

import java.util.List;
import java.util.Map;

public class Eventweek {
	private Integer week;
	private List<Integer> months;
	private Map<Integer, Eventday> days;

	// Getters and setters

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public List<Integer> getMonths() {
		return months;
	}

	public void setMonths(List<Integer> months) {
		this.months = months;
	}

	public Map<Integer, Eventday> getDays() {
		return days;
	}

	public void setDays(Map<Integer, Eventday> days) {
		this.days = days;
	}
}
