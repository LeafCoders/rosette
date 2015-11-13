package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.model.education.EducationTypeRef;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class EducationThemeService extends MongoTemplateCRUD<EducationTheme> {

    @Autowired
    private EducationTypeService educationTypeService;
    @Autowired
    private UploadService uploadService;

	public EducationThemeService() {
		super(EducationTheme.class, PermissionType.EDUCATION_THEMES);
	}

	@Override
	public void insertDependencies(EducationTheme educationTheme) {
        if (educationTheme.getEducationType() != null) {
            educationTheme.setEducationType(new EducationTypeRef(educationTypeService.read(educationTheme.getEducationType().getId())));
        }
        if (educationTheme.getImage() != null) {
            educationTheme.setImage(uploadService.read(educationTheme.getImage().getId()));
        }
	}
}
