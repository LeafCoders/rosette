package se.leafcoders.rosette.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.model.UploadFolder;

@Service
public class LocationService extends MongoTemplateCRUD<Location> {

	@Autowired
	private UploadService uploadService;

	@Autowired
	public LocationService(UploadFolderService uploadFolderService) {
		super("locations", Location.class);

		UploadFolder folder = new UploadFolder();
		folder.setTitle("uploadFolder.locations");
		folder.setName("locations");
		folder.setIsPublic(true);
		folder.setMimeTypes(Arrays.asList(new String[]{"image/"}));
		uploadFolderService.addFolder(folder);
	}

	@Override
	public void insertDependencies(Location data) {
		if (data.getDirectionImage() != null) {
			data.setDirectionImage(uploadService.read(data.getDirectionImage().getId()));
		}
	}
}
