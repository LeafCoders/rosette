package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.model.education.EducationThemeRef;
import se.leafcoders.rosette.model.education.EducationTypeRef;
import se.leafcoders.rosette.model.education.EventEducation;
import se.leafcoders.rosette.model.reference.EventRef;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class EducationService extends MongoTemplateCRUD<Education> {

    @Autowired
    private EducationTypeService educationTypeService;
    @Autowired
    private EducationThemeService educationThemeService;
    @Autowired
    private EventService eventService;

	public EducationService() {
		super(Education.class, PermissionType.EDUCATIONS);
	}

	@Override
	public void insertDependencies(Education data) {
        if (data.getEducationType() != null) {
            data.setEducationType(new EducationTypeRef(educationTypeService.read(data.getEducationType().getId())));
        }
        if (data.getEducationTheme() != null) {
            data.setEducationTheme(new EducationThemeRef(educationThemeService.read(data.getEducationTheme().getId())));
        }
	    if (data instanceof EventEducation) {
            EventEducation education = (EventEducation) data;
            if (education.getEvent() != null) {
                education.setEvent(new EventRef(eventService.read(education.getEvent().getId())));
            }
		}
	}
}
