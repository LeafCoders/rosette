package se.ryttargardskyrkan.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.resource.Resource;
import se.ryttargardskyrkan.rosette.model.resource.ResourceMethods;
import se.ryttargardskyrkan.rosette.model.resource.UploadResource;
import se.ryttargardskyrkan.rosette.model.resource.UploadResourceMethods;
import se.ryttargardskyrkan.rosette.model.resource.UserResource;
import se.ryttargardskyrkan.rosette.model.resource.UserResourceMethods;

@Service
public class MethodsService {
	@Autowired
	UserService userService; 
	@Autowired
	GroupService groupService; 
	@Autowired
	UploadService uploadService;

	public ResourceMethods of(Resource resource) {
		if (resource instanceof UserResource) {
			return new UserResourceMethods((UserResource)resource, userService, groupService);
		} else if (resource instanceof UploadResource) {
			return new UploadResourceMethods((UploadResource)resource, uploadService);
		}
		throw new UnsupportedOperationException();
	}
}
