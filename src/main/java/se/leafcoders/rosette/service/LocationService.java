package se.leafcoders.rosette.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.model.upload.UploadFolder;

@Service
public class LocationService extends MongoTemplateCRUD<Location> {

	@Autowired
	private UploadService uploadService;

	@Autowired
	public LocationService(UploadFolderService uploadFolderService) {
		super("locations", Location.class);

		UploadFolder folder = new UploadFolder();
		folder.setId("locations");
		folder.setName("uploadFolder.locations");
		folder.setIsPublic(true);
		folder.setMimeTypes(Arrays.asList(new String[]{"image/"}));
		uploadFolderService.addStaticFolder(folder);
	}

	@Override
	public void insertDependencies(Location data) {
		if (data.getDirectionImage() != null) {
			data.setDirectionImage(uploadService.read(data.getDirectionImage().getId()));
		}
	}
}
