package se.leafcoders.rosette.comparator;

import java.util.Comparator;
import org.joda.time.DateTime;
import org.joda.time.Days;
import se.leafcoders.rosette.model.Poster;

public class PosterComparator implements Comparator<Poster> {

	private final DateTime now = new DateTime();

	@Override
    public int compare(Poster poster1, Poster poster2) {
    	int days1 = daysValue(poster1);
    	int days2 = daysValue(poster2);
    	if (days1 < days2) {
    		return -1;
    	} else if (days1 == days2) {
    		return 0;
    	}
        return 1;
    }
    
    /*
     * Returns number of days since or until:
     * - >100 for posters that have passed their endTime 
     * - [0,100] for posters that is active
     * - <0 for posters that is going to be active 
     */
    private int daysValue(Poster poster) {
    	final DateTime endTime = new DateTime(poster.getEndTime());
    	if (endTime.isBefore(now)) {
    		return 100 + Days.daysBetween(endTime, now).getDays();
    	}
    	final DateTime startTime = new DateTime(poster.getStartTime());
    	if (startTime.isBefore(now)) {
    		return 100 - Math.min(100, Days.daysBetween(now, endTime).getDays());
    	}
    	return Days.daysBetween(startTime, now).getDays();
    }
} 
