package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Location;

@Service
public class LocationService extends MongoTemplateCRUD<Location> {

	@Autowired
	private UploadService uploadService;

	public LocationService() {
		super("locations", Location.class);
	}

	@Override
	public void insertDependencies(Location data) {
		if (data.getDirectionImage() != null) {
			data.setDirectionImage(uploadService.read(data.getDirectionImage().getId()));
		}
	}
}
