package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.model.education.EducationType;
import se.leafcoders.rosette.model.education.EducationTypeRef;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionResult;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;

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
    protected PermissionResult permissionResultFor(PermissionAction actionType, EducationTheme educationTheme) {
        if (educationTheme != null && educationTheme.getEducationType() != null) {
            return security.permissionResultFor(
                    new PermissionValue(PermissionType.EDUCATION_THEMES_EDUCATION_TYPES, actionType, educationTheme.getEducationType().getId()),
                    new PermissionValue(PermissionType.EDUCATION_THEMES, actionType, educationTheme.getId()));
        }
        return super.permissionResultFor(actionType, educationTheme);
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
