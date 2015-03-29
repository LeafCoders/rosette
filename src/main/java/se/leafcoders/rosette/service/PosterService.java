package se.leafcoders.rosette.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.model.upload.UploadFolder;

@Service
public class PosterService extends MongoTemplateCRUD<Poster> {

	@Autowired
	private UploadService uploadService;

	@Autowired
	public PosterService(UploadFolderService uploadFolderService) {
		super("posters", Poster.class);

		UploadFolder folder = new UploadFolder();
		folder.setId("posters");
		folder.setName("uploadFolder.posters");
		folder.setIsPublic(true);
		folder.setMimeTypes(Arrays.asList(new String[]{"image/"}));
		uploadFolderService.addStaticFolder(folder);
	}

	@Override
	public void insertDependencies(Poster data) {
		if (data.getImage() != null) {
			data.setImage(uploadService.read(data.getImage().getId()));
		}
	}
}
