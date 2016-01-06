package se.leafcoders.rosette.controller.publicdata;

import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.Booking;
import se.leafcoders.rosette.service.BookingService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class PublicBookingController extends PublicDataController {

    @Autowired
    private BookingService bookingService;

	@RequestMapping(value = "bookings", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Booking> getBookings(HttpServletRequest request) {
		checkPermission();

		ManyQuery manyQuery = new ManyQuery(request, "startTime");
		manyQuery.addCriteria(activeOrFutureBookingsTodayCriteria());
		return bookingService.readMany(manyQuery, false);
	}	

	private Criteria activeOrFutureBookingsTodayCriteria() {
		final Calendar now = Calendar.getInstance();
		Calendar endOfDay = Calendar.getInstance();
		endOfDay.set(Calendar.HOUR_OF_DAY, 23);
		endOfDay.set(Calendar.MINUTE, 59);
		endOfDay.set(Calendar.SECOND, 59);

		return Criteria.where("endTime").gt(now.getTime()).and("startTime").lt(endOfDay.getTime());
	}
	
}
