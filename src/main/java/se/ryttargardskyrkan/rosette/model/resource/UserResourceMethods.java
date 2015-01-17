package se.ryttargardskyrkan.rosette.model.resource;

import org.springframework.data.mongodb.core.query.Update;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.ObjectReferencesAndText;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import se.ryttargardskyrkan.rosette.service.GroupService;
import se.ryttargardskyrkan.rosette.service.UserService;

public class UserResourceMethods implements ResourceMethods {
	protected final UserResource resource;
	UserService userService;
	GroupService groupService;

	public UserResourceMethods(UserResource resource, UserService userService, GroupService groupService) {
		this.resource = resource;
		this.groupService = groupService;
	}

	public Update createAssignUpdate(ResourceType resourceType) {
		if (resourceType instanceof UserResourceType) {
			UserResourceType userResourceType = (UserResourceType) resourceType;

			int numUsers = resource.getUsers().totalNumRefsAndText();
			if (!userResourceType.getMultiSelect() && numUsers > 1) {
				throw new SimpleValidationException(new ValidationError("resource", "userResource.multiUsersNotAllowed"));
			}

			if (!userResourceType.getAllowText() && resource.getUsers().hasText()) {
				throw new SimpleValidationException(new ValidationError("resource", "userResource.userByTextNotAllowed"));
			}
			
			String groupId = userResourceType.getGroup().getIdRef();
			if (groupService.containsUsers(groupId, resource.getUsers().getRefs())) {
				return new Update().set("resources.$.users", resource.getUsers());
			}
		}
		throw new SimpleValidationException(new ValidationError("resource", "userResource.assignedUserNotInGroup"));
	}
	
	public void insertDependencies() {
		final ObjectReferencesAndText<User> userRefs = resource.getUsers();
		if (userRefs != null) {
			for (ObjectReference<User> userRef : userRefs.getRefs()) {
				userRef.setReferredObject(userService.readNoDep(userRef.getIdRef()));
			}
		}
	}
}
