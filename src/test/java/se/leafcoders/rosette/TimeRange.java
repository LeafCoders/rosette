package se.leafcoders.rosette;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeRange {
    
    private LocalDateTime start = null;
    private LocalDateTime end = null;
    
    private TimeRange(LocalDateTime start) {
        this.start = start;
    }
    
    public static TimeRange start(int weekDay, int hour, int minute) {
        LocalDate date = LocalDate.now().plusDays(weekDay - LocalDate.now().getDayOfWeek().getValue());
        return new TimeRange(LocalDateTime.of(date, LocalTime.of(hour, minute)));
    }

    public TimeRange weekOffset(int weeks) {
        this.start = this.start.plusWeeks(weeks);
        return this;
    }

    public TimeRange endAfterMinutes(int minuteOffset) {
        this.end = this.start.plusMinutes(minuteOffset);
        return this;
    }
    
    public TimeRange endAfterDays(int dayOffset) {
        this.end = this.start.plusDays(dayOffset);
        return this;
    }
    
    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
