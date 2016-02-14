package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Booking;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class BookingService extends MongoTemplateCRUD<Booking> {

	@Autowired
	private LocationService locationService;

	public BookingService() {
		super(Booking.class, PermissionType.BOOKINGS);
	}

	@Override
	public void setReferences(Booking data, Booking dataInDb, boolean checkPermissions) {
		if (data.getLocation() != null && data.getLocation().hasRef()) {
			data.getLocation().setRef(locationService.read(data.getLocation().refId(), checkPermissions));
		}
	}

	@Override
    public Class<?>[] references() {
        return new Class<?>[] { Location.class };
    }
}
