package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.Booking;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.service.BookingService;
import se.leafcoders.rosette.service.SecurityService;

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
	public List<Booking> getBookings(HttpServletResponse response) {
		Query query = new Query().with(new Sort(Sort.Direction.ASC, "startTime"));
		return bookingService.readMany(query);
	}

	@RequestMapping(value = "bookings", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Booking postBooking(@RequestBody Booking booking, HttpServletResponse response) {
		return bookingService.create(booking, response);
	}

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putBooking(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		bookingService.update(id, request, response);
	}

	@RequestMapping(value = "bookings/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBooking(@PathVariable String id, HttpServletResponse response) {
		bookingService.delete(id, response);
	}

	@RequestMapping(value = "bookings", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteBookings(HttpServletResponse response) {
		security.checkPermission(new PermissionValue(PermissionType.BOOKINGS, PermissionAction.DELETE));
		mongoTemplate.dropCollection("bookings");
		response.setStatus(HttpStatus.OK.value());
	}
}
