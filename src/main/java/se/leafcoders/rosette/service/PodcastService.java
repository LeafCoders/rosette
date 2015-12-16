package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.EducationType;
import se.leafcoders.rosette.model.education.EducationTypeRef;
import se.leafcoders.rosette.model.podcast.Podcast;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class PodcastService extends MongoTemplateCRUD<Podcast> {

    @Autowired
    private EducationTypeService educationTypeService;

    @Autowired
    private UploadService uploadService;

	public PodcastService() {
		super(Podcast.class, PermissionType.PODCASTS);
	}

	@Override
	public void setReferences(Podcast podcast, boolean checkPermissions) {
        if (podcast.getEducationType() != null) {
            podcast.setEducationType(new EducationTypeRef(educationTypeService.read(podcast.getEducationType().getId(), checkPermissions)));
        }
        if (podcast.getImage() != null) {
            podcast.setImage(uploadService.read(podcast.getImage().getId(), checkPermissions));
        }
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { EducationType.class };
    }
}
