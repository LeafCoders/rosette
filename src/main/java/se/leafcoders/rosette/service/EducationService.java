package se.leafcoders.rosette.service;

import java.util.Optional;
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
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;

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
    protected void checkPermission(PermissionAction actionType, Education education) {
        if (education.getEducationType() != null) {
            security.checkPermission(
                    new PermissionValue(PermissionType.EDUCATIONS_EDUCATION_TYPES, actionType, education.getEducationType().getId()),
                    new PermissionValue(PermissionType.EDUCATIONS, actionType, education.getId()));
        } else {
            super.checkPermission(actionType, education);
        }
    }

    @Override
    public Education create(Education education, HttpServletResponse response) {
        if (education.getType() == "event") {
            setDataFromEvent((EventEducation) education, null, true);
        }
        return super.create(education, response);
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
    protected void afterSetReferences(Education updateData, Education dataInDatabase, boolean checkPermissions) {
        if (updateData.getType() == "event") {
            setDataFromEvent((EventEducation) updateData, (EventEducation) dataInDatabase, checkPermissions);
        }
    }

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { EducationType.class, EducationTheme.class, Event.class };
    }

	private void setDataFromEvent(EventEducation eventEducation, EventEducation educationInDatabase, boolean checkPermissions) {
        if (eventEducation.getEvent() != null) {
            Event event = eventService.read(eventEducation.getEvent().getId(), checkPermissions);
            eventEducation.setAuthorName(getAuthorName(event, eventEducation, educationInDatabase));
            eventEducation.setTime(event.getStartTime());
        }
	}
	
	private String getAuthorName(Event event, EventEducation education, EventEducation educationInDatabase) {
	    final EducationType educationType = getEducationType(education, educationInDatabase);
        if (educationType != null && event.getResources() != null) {
            Optional<Resource> userResource = event.getResources().stream().filter((Resource resource) -> {
                return resource.getResourceType().getId().equals(educationType.getAuthorResourceType().getId());
            }).findAny();
            if (userResource.isPresent()) {
                return ((UserResource) userResource.get()).getUsers().namesString();
            }
        }
	    return null;
	}
	
	private EducationType getEducationType(EventEducation education, EventEducation educationInDatabase) {
        EducationTypeRef educationTypeRef = education.getEducationType();
        if (educationTypeRef == null && educationInDatabase != null) {
            educationTypeRef = educationInDatabase.getEducationType();
        }
        if (educationTypeRef != null) {
            return educationTypeService.read(educationTypeRef.getId());
        }
        return null;
	}
}
