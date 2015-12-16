package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.model.education.EducationType;
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
	public void setReferences(EducationTheme educationTheme, boolean checkPermissions) {
        if (educationTheme.getEducationType() != null) {
            educationTheme.setEducationType(new EducationTypeRef(educationTypeService.read(educationTheme.getEducationType().getId(), checkPermissions)));
        }
        if (educationTheme.getImage() != null) {
            educationTheme.setImage(uploadService.read(educationTheme.getImage().getId(), checkPermissions));
        }
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { EducationType.class };
    }
}
