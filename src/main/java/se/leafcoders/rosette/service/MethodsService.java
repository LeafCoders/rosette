package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.model.resource.ResourceMethods;
import se.leafcoders.rosette.model.resource.UploadResource;
import se.leafcoders.rosette.model.resource.UploadResourceMethods;
import se.leafcoders.rosette.model.resource.UserResource;
import se.leafcoders.rosette.model.resource.UserResourceMethods;

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
