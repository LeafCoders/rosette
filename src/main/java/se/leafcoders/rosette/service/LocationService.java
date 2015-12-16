package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class LocationService extends MongoTemplateCRUD<Location> {

	@Autowired
	private UploadService uploadService;

	public LocationService() {
		super(Location.class, PermissionType.LOCATIONS);
	}

	@Override
	public void setReferences(Location data, boolean checkPermissions) {
		if (data.getDirectionImage() != null) {
			data.setDirectionImage(uploadService.read(data.getDirectionImage().getId(), checkPermissions));
		}
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { };
    }
}
