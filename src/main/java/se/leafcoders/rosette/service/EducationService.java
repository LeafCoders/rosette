package se.leafcoders.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.model.education.EducationThemeRef;
import se.leafcoders.rosette.model.education.EducationType;
import se.leafcoders.rosette.model.education.EducationTypeRef;
import se.leafcoders.rosette.model.education.EventEducation;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.reference.EventRef;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.model.resource.UserResource;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class EducationService extends MongoTemplateCRUD<Education> {

    @Autowired
    private EducationTypeService educationTypeService;
    @Autowired
    private EducationThemeService educationThemeService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UploadService uploadService;

	public EducationService() {
		super(Education.class, PermissionType.EDUCATIONS);
	}

    @Override
    public Education create(Education education, HttpServletResponse response) {
        setDataFromEvent(education, null, true);
        return super.create(education, response);
    }

    @Override
    protected void afterSetReferences(Education updateData, Education dataInDatabase, boolean checkPermissions) {
        setDataFromEvent(updateData, dataInDatabase, checkPermissions);
    }

	@Override
	public void setReferences(Education data, boolean checkPermissions) {
        if (data.getEducationType() != null) {
            data.setEducationType(new EducationTypeRef(educationTypeService.read(data.getEducationType().getId(), checkPermissions)));
        }
        if (data.getEducationTheme() != null) {
            data.setEducationTheme(new EducationThemeRef(educationThemeService.read(data.getEducationTheme().getId(), checkPermissions)));
        }
        if (data.getRecording() != null) {
            data.setRecording(uploadService.read(data.getRecording().getId(), checkPermissions));
        }
        
	    if (data instanceof EventEducation) {
            EventEducation education = (EventEducation) data;
            if (education.getEvent() != null) {
                education.setEvent(new EventRef(eventService.read(education.getEvent().getId(), checkPermissions)));
            }
		}
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { EducationType.class, EducationTheme.class, Event.class };
    }

	private void setDataFromEvent(Education education, Education dataInDatabase, boolean checkPermissions) {
        if (education.getType() == "event") {
            EventEducation eventEducation = (EventEducation) education;
            if (eventEducation.getEvent() != null) {
                Event event = eventService.read(eventEducation.getEvent().getId(), checkPermissions);
                if (!setAuthorName(event, eventEducation, eventEducation)) {
                    if (dataInDatabase == null || !setAuthorName(event, eventEducation, (EventEducation)dataInDatabase)) {
                        education.setAuthorName(null);
                    }
                }
                education.setEducationTime(event.getStartTime());
            }
        }
	}
	
	private boolean setAuthorName(Event event, EventEducation educationToUpdate, EventEducation education) {
        if (education.getEducationType() != null && event.getResources() != null) {
            EducationType educationType = educationTypeService.read(education.getEducationType().getId());
            return event.getResources().stream().anyMatch((Resource resource) -> {
                if (resource.getResourceType().getId().equals(educationType.getAuthorResourceType().getId())) {
                    educationToUpdate.setAuthorName(((UserResource) resource).getUsers().namesString());
                    return true;
                }
                return false;
            });
        }
	    return false;
	}
}
