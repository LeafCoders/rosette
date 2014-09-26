package se.ryttargardskyrkan.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Booking;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.model.ObjectReferenceOrText;

@Service
public class BookingService extends MongoTemplateCRUD<Booking> {

	@Autowired
	private LocationService locationService;

	public BookingService() {
		super("bookings", Booking.class);
	}
	
	@Override
	public void insertDependencies(Booking data) {
		final ObjectReferenceOrText<Location> locationRef = data.getLocation(); 
		if (locationRef != null && locationRef.hasIdRef()) {
			locationRef.setReferredObject(locationService.readNoDep(locationRef.getIdRef()));
		}
	}
}
