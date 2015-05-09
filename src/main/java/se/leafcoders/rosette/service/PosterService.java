package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class PosterService extends MongoTemplateCRUD<Poster> {

	@Autowired
	private UploadService uploadService;

	public PosterService() {
		super(Poster.class, PermissionType.POSTERS);
	}

	@Override
	public void insertDependencies(Poster data) {
		if (data.getImage() != null) {
			data.setImage(uploadService.read(data.getImage().getId()));
		}
	}
}
