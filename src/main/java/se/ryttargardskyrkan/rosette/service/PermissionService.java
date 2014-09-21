package se.ryttargardskyrkan.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.security.MongoRealm;

@Service
public class PermissionService extends MongoTemplateCRUD<Permission> {

	@Autowired
	private MongoRealm mongoRealm;
	@Autowired
	private UserService userService;
	@Autowired
	private GroupService groupService;

	public PermissionService() {
		super("permissions", Permission.class);
	}

	@Override
	public Permission create(Permission data, HttpServletResponse response) {
		// Clearing auth cache
		mongoRealm.clearCache(null);

		return super.create(data, response);
	}

	@Override
	public void update(String id, Permission data, Update update, HttpServletResponse response) {
		// Clearing auth cache
		mongoRealm.clearCache(null);

		super.update(id, data, update, response);
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		// Clearing auth cache
		mongoRealm.clearCache(null);

		super.delete(id, response);
	}

	@Override
	public void insertDependencies(Permission data) {
		final ObjectReference<User> userRef = data.getUser(); 
		if (userRef != null) {
			userRef.setReferredObject(userService.read(userRef.getIdRef()));
		}
		final ObjectReference<Group> groupRef = data.getGroup(); 
		if (groupRef != null) {
			groupRef.setReferredObject(groupService.read(groupRef.getIdRef()));
		}
	}
}
