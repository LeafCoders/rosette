package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Poster;

@Service
public class PosterService extends MongoTemplateCRUD<Poster> {

	@Autowired
	private UploadService uploadService;

	public PosterService() {
		super("posters", Poster.class);
	}

	@Override
	public void insertDependencies(Poster data) {
		if (data.getImage() != null) {
			data.setImage(uploadService.read(data.getImage().getId()));
		}
	}
}
