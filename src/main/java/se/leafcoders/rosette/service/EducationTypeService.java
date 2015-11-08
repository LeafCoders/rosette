package se.leafcoders.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.education.EducationType;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class EducationTypeService extends MongoTemplateCRUD<EducationType> {

    @Autowired
    private EventTypeService eventTypeService;
	@Autowired
	private ResourceTypeService resourceTypeService;

	public EducationTypeService() {
		super(EducationType.class, PermissionType.EDUCATION_TYPES);
	}

	@Override
	public EducationType create(EducationType data, HttpServletResponse response) {
		validateUniqueId(data);
		return super.create(data, response);
	}

	@Override
	public void insertDependencies(EducationType educationType) {
        if (educationType.getEventType() != null) {
            educationType.setEventType(eventTypeService.read(educationType.getEventType().getId()));
        }
		if (educationType.getAuthorResourceType() != null) {
			educationType.setAuthorResourceType(resourceTypeService.readUserResourceType(educationType.getAuthorResourceType().getId()));
		}
	}
}
