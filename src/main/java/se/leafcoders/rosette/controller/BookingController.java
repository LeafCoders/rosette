package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.model.Booking;
import se.leafcoders.rosette.service.BookingService;
import se.leafcoders.rosette.service.SecurityService;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

@Controller
public class BookingController extends AbstractController {
    @Autowired
    private BookingService bookingService;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SecurityService security;

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Booking getBooking(@PathVariable String id) {
		return bookingService.read(id);
	}

	@RequestMapping(value = "bookings", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Booking> getBookings(@RequestParam(defaultValue = "false") boolean onlyActiveToday, HttpServletResponse response) {
		Query query = new Query().with(new Sort(Sort.Direction.ASC, "startTime"));
		if (onlyActiveToday) {
			query.addCriteria(activeTodayCriteria());
		}
		return bookingService.readMany(query);
	}

	@RequestMapping(value = "bookings", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Booking postBooking(@RequestBody Booking booking, HttpServletResponse response) {
		return bookingService.create(booking, response);
	}

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putBooking(@PathVariable String id, @RequestBody Booking booking, HttpServletResponse response) {
		bookingService.update(id, booking, response);
	}

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBooking(@PathVariable String id, HttpServletResponse response) {
		bookingService.delete(id, response);
	}

	@RequestMapping(value = "bookings", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBookings(HttpServletResponse response) {
		security.checkPermission("delete:bookings");
		mongoTemplate.dropCollection("bookings");
		response.setStatus(HttpStatus.OK.value());
	}
	
	private Criteria activeTodayCriteria() {
		final Calendar now = Calendar.getInstance();
		Calendar endOfDay = Calendar.getInstance();
		endOfDay.set(Calendar.HOUR_OF_DAY, 23);
		endOfDay.set(Calendar.MINUTE, 59);
		endOfDay.set(Calendar.SECOND, 59);

		return Criteria.where("endTime").gt(now.getTime()).and("startTime").lt(endOfDay.getTime());
	}
}
