package se.leafcoders.rosette.model.resource;

import org.springframework.data.mongodb.core.query.Update;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.reference.UserRef;
import se.leafcoders.rosette.service.GroupService;
import se.leafcoders.rosette.service.UserService;

public class UserResourceMethods implements ResourceMethods {
	protected final UserResource resource;
	protected UserService userService;
	protected GroupService groupService;

	public UserResourceMethods(UserResource resource, UserService userService, GroupService groupService) {
		this.resource = resource;
		this.userService = userService;
		this.groupService = groupService;
	}

	public Update updateQuery(ResourceType resourceTypeIn, boolean checkPermissions) {
		validateAndUpdate(resourceTypeIn, checkPermissions);
		return new Update().set("resources.$.users", resource.getUsers());
	}

	public void setReferences(boolean checkPermissions) {
		validateAndUpdate(resource.getResourceType(), checkPermissions);
	}

	private void validateAndUpdate(ResourceType resourceType, boolean checkPermissions) {
		if (!(resource.getResourceType() instanceof UserResourceType)) {
			throw new SimpleValidationException(new ValidationError("resource", "userResource.wrongResourceType"));
		}

		if (resource.getUsers() != null) {
			UserResourceType userResourceType = (UserResourceType) resource.getResourceType();

			int numUsers = resource.getUsers().totalNumRefsAndText();
			if (userResourceType.getMultiSelect() != Boolean.TRUE && numUsers > 1) {
				throw new SimpleValidationException(new ValidationError("resource", "userResource.multiUsersNotAllowed"));
			}

			if (userResourceType.getAllowText() != Boolean.TRUE && resource.getUsers().hasText()) {
				throw new SimpleValidationException(new ValidationError("resource", "userResource.userByTextNotAllowed"));
			}

			String groupId = userResourceType.getGroup().getId();
			if (resource.getUsers().hasRefs() && !groupService.containsUsers(groupId, resource.getUsers().getRefs())) {
				throw new SimpleValidationException(new ValidationError("resource", "userResource.userDoesNotExistInGroup"));
			}

			if (resource.getUsers().getRefs() != null) {
    			for (UserRef user : resource.getUsers().getRefs()) {
    				resource.getUsers().updateRef(userService.readAsRef(user.getId(), checkPermissions));
    			}
			}
		}
	}
}
