package se.ryttargardskyrkan.rosette.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.Poster;
import se.ryttargardskyrkan.rosette.model.UploadFolder;
import se.ryttargardskyrkan.rosette.model.UploadResponse;

@Service
public class PosterService extends MongoTemplateCRUD<Poster> {

	@Autowired
	private UploadService uploadService;

	@Autowired
	public PosterService(UploadFolderService uploadFolderService) {
		super("posters", Poster.class);

		UploadFolder folder = new UploadFolder();
		folder.setTitle("posterItems.label.title");
		folder.setName("posters");
		folder.setMimeTypes(Arrays.asList(new String[]{"image/"}));
		uploadFolderService.addFolder(folder);
	}
	
	@Override
	public void insertDependencies(Poster data) {
		final ObjectReference<UploadResponse> imageRef = data.getImage(); 
		if (imageRef != null && imageRef.hasIdRef()) {
			imageRef.setReferredObject(uploadService.read(imageRef.getIdRef()));
		}
	}
}
