package se.ryttargardskyrkan.rosette.comparator;

import static org.junit.Assert.assertEquals;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.model.Poster;

public class PosterComparatorTest {
	
	@Test
	public void testPosterOrder() throws ParseException {
		Poster wasActive10DaysAgo = new Poster();
		wasActive10DaysAgo.setStartTime(new DateTime().minusDays(16).toDate());
		wasActive10DaysAgo.setEndTime(new DateTime().minusDays(10).toDate());

		Poster wasActive7DaysAgo = new Poster();
		wasActive7DaysAgo.setStartTime(new DateTime().minusDays(20).toDate());
		wasActive7DaysAgo.setEndTime(new DateTime().minusDays(7).toDate());

		Poster active3DaysMore = new Poster();
		active3DaysMore.setStartTime(new DateTime().minusDays(6).toDate());
		active3DaysMore.setEndTime(new DateTime().plusDays(3).toDate());

		Poster active4DaysMore = new Poster();
		active4DaysMore.setStartTime(new DateTime().minusDays(7).toDate());
		active4DaysMore.setEndTime(new DateTime().plusDays(4).toDate());

		Poster soonActiveIn4Days = new Poster();
		soonActiveIn4Days.setStartTime(new DateTime().plusDays(4).toDate());
		soonActiveIn4Days.setEndTime(new DateTime().plusDays(7).toDate());

		Poster soonActiveIn10Days = new Poster();
		soonActiveIn10Days.setStartTime(new DateTime().plusDays(10).toDate());
		soonActiveIn10Days.setEndTime(new DateTime().plusDays(12).toDate());

		List<Poster> posters = new ArrayList<Poster>();
		posters.add(active4DaysMore);
		posters.add(wasActive10DaysAgo);
		posters.add(active3DaysMore);
		posters.add(soonActiveIn10Days);
		posters.add(wasActive7DaysAgo);
		posters.add(soonActiveIn4Days);
		
        Collections.sort(posters, new PosterComparator());

        // First posters that are going to be active. The top most has most days until being active 
		assertEquals(soonActiveIn10Days, posters.get(0));
		assertEquals(soonActiveIn4Days, posters.get(1));
        // Posters that is active. The one with most days left of active is first
		assertEquals(active4DaysMore, posters.get(2));
		assertEquals(active3DaysMore, posters.get(3));
        // Posters that have been active. The one with least days since not active is first
		assertEquals(wasActive7DaysAgo, posters.get(4));
		assertEquals(wasActive10DaysAgo, posters.get(5));
	}
}
