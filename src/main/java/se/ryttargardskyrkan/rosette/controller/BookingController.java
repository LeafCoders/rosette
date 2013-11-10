package se.ryttargardskyrkan.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Booking;
import se.ryttargardskyrkan.rosette.model.Location;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class BookingController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;

    @Autowired
    private LocationController locationController;

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Booking getBooking(@PathVariable String id) {
		checkPermission("read:bookings:" + id);

        Booking booking = mongoTemplate.findById(id, Booking.class);
		if (booking == null) {
			throw new NotFoundException();
		}

        insertDependenciesIntoBooking(booking);

		return booking;
	}

	@RequestMapping(value = "bookings", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Booking> getBookings(@RequestParam(defaultValue = "false") boolean onlyActiveToday, HttpServletResponse response) {
		Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, "startTime"));
        final List<Booking> bookingsInDatabase = mongoTemplate.find(query, Booking.class);
		List<Booking> bookings = new ArrayList<Booking>();
		if (bookingsInDatabase != null) {
			for (Booking booking : bookingsInDatabase) {
				if (isPermitted("read:bookings:" + booking.getId())) {
					if (filterActiveToday(booking, onlyActiveToday)) {
						insertDependenciesIntoBooking(booking);
						bookings.add(booking);
					}
				}
			}
		}
		return bookings;
	}

	@RequestMapping(value = "bookings", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Booking postBooking(@RequestBody Booking booking, HttpServletResponse response) {
		checkPermission("create:bookings");
		validate(booking);
		
		mongoTemplate.insert(booking);
		
		response.setStatus(HttpStatus.CREATED.value());
		return booking;
	}

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putBooking(@PathVariable String id, @RequestBody Booking booking, HttpServletResponse response) {
		checkPermission("update:bookings:" + id);
		validate(booking);

		Update update = new Update();
		update.set("customerName", booking.getCustomerName());
		update.set("startTime", booking.getStartTime());
		update.set("endTime", booking.getEndTime());
		update.set("location", booking.getLocation());

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Booking.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBooking(@PathVariable String id, HttpServletResponse response) {
		checkPermission("delete:bookings:" + id);

        Booking deletedBooking = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Booking.class);
		if (deletedBooking == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}
	}

	@RequestMapping(value = "bookings", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBookings(HttpServletResponse response) {
		checkPermission("delete:bookings");

		mongoTemplate.dropCollection("bookings");
		response.setStatus(HttpStatus.OK.value());
	}
	
	public void insertDependenciesIntoBooking(final Booking booking) {
		if (booking.getLocation().hasIdRef()) {
	        Location location = mongoTemplate.findById(booking.getLocation().getIdRef(), Location.class);
	        if (location == null) {
	            throw new NotFoundException();
	        }
	        booking.getLocation().setReferredObject(location);
	        locationController.insertDependenciesIntoLocation(location);
		}
	}

	private boolean filterActiveToday(Booking booking, boolean onlyActiveToday) {
		if (onlyActiveToday) {
			final Calendar now = Calendar.getInstance();
			Calendar endOfDay = Calendar.getInstance();
			endOfDay.set(Calendar.HOUR_OF_DAY, 23);
			endOfDay.set(Calendar.MINUTE, 59);
			endOfDay.set(Calendar.SECOND, 59);
			return booking.getStartTime().before(endOfDay.getTime()) && booking.getEndTime().after(now.getTime());
		}
		return true;
	}
}
