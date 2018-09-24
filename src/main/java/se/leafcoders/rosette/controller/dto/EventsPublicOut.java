package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import se.leafcoders.rosette.persistence.model.Event;

public class EventsPublicOut {

    private Integer year;
    private Integer week;
    private String fromDate;
    private String untilDate;
    private List<DayData> days = new ArrayList<>();
    
    private static class DayData {
        private String date;
        private Integer weekDay;
        private List<EventData> events = new ArrayList<>();
        
        public DayData(LocalDateTime date) {
            this.date = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            weekDay = date.getDayOfWeek().getValue();
        }
        
        public void addEvent(Event event) {
            events.add(new EventData(event));
        }

        @SuppressWarnings("unused")
        public String getDate() {
            return date;
        }

        @SuppressWarnings("unused")
        public Integer getWeekDay() {
            return weekDay;
        }

        @SuppressWarnings("unused")
        public List<EventData> getEvents() {
            return events;
        }
    }
    
    private static class EventData {
        private String title;
        private String description;
        private String startTime;
        private String endTime;
        
        public EventData(Event event) {
            title = event.getTitle();
            description = event.getDescription();
            if (event.getStartTime() != null) {
                startTime = event.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
            }
            if (event.getEndTime() != null) {
                endTime = event.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
            }
        }

        @SuppressWarnings("unused")
        public String getTitle() {
            return title;
        }

        @SuppressWarnings("unused")
        public String getDescription() {
            return description;
        }

        @SuppressWarnings("unused")
        public String getStartTime() {
            return startTime;
        }

        @SuppressWarnings("unused")
        public String getEndTime() {
            return endTime;
        }
    }


    public EventsPublicOut(LocalDateTime from, LocalDateTime before, List<Event> events) {
        year = from.getYear();
        week = from.get(WeekFields.ISO.weekOfWeekBasedYear());
        fromDate = from.format(DateTimeFormatter.ISO_LOCAL_DATE);
        untilDate = before.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        createDays(from, before, events);
    }

    
    private void createDays(LocalDateTime from, LocalDateTime before, List<se.leafcoders.rosette.persistence.model.Event> events) {
        for (int i = 0; from.plusDays(i).isBefore(before); i++) {
            days.add(new DayData(from.plusDays(i)));
        }
        events.forEach((se.leafcoders.rosette.persistence.model.Event event) -> {
            days.get((int) from.until(event.getStartTime(), ChronoUnit.DAYS)).addEvent(event);
        });
    }


    // Getters and setters

    public Integer getYear() {
        return year;
    }

    public Integer getWeek() {
        return week;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getUntilDate() {
        return untilDate;
    }

    public List<DayData> getDays() {
        return days;
    }
}
