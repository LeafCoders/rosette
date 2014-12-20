package se.ryttargardskyrkan.rosette.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
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
			userRef.setReferredObject(userService.readNoDep(userRef.getIdRef()));
		}
		final ObjectReference<Group> groupRef = data.getGroup(); 
		if (groupRef != null) {
			groupRef.setReferredObject(groupService.readNoDep(groupRef.getIdRef()));
		}
	}
	
	public List<String> getForUser(String userId) {
		List<String> permissions = new LinkedList<String>();
		
		// Adding permissions for everyone
		permissions.addAll(getForEveryone());

		// Adding permissions for specified user
		if (userId == null || userId.isEmpty()) {
			return permissions;
		}
		User user = mongoTemplate.findById(userId, User.class);
		if (user != null) {
			// Adding group permissions
			Query groupMembershipsQuery = new Query(Criteria.where("user.idRef").is(user.getId()));
			List<GroupMembership> groupMemberships = mongoTemplate.find(groupMembershipsQuery, GroupMembership.class);
			if (groupMemberships != null) {
				List<String> groupIds = new ArrayList<String>();
				for (GroupMembership groupMembership : groupMemberships) {
					groupIds.add(groupMembership.getGroup().getIdRef());
				}
	
				Query groupPermissionQuery = Query.query(Criteria.where("group.idRef").in(groupIds));
				List<Permission> groupPermissions = mongoTemplate.find(groupPermissionQuery, Permission.class);
				if (groupPermissions != null) {
					for (Permission groupPermission : groupPermissions) {
						if (groupPermission.getPatterns() != null) {
							permissions.addAll(groupPermission.getPatterns());
						}
					}
				}
			}
	
			// Adding user permissions
			Query userPermissionQuery = Query.query(Criteria.where("user.idRef").is(user.getId()));
			List<Permission> userPermissions = mongoTemplate.find(userPermissionQuery, Permission.class);
			if (userPermissions != null) {
				for (Permission userPermission : userPermissions) {
					if (userPermission.getPatterns() != null) {
						permissions.addAll(userPermission.getPatterns());
					}
				}
			}
		}
		return permissions;
	}
	
	public List<String> getForEveryone() {
		Query query = Query.query(Criteria.where("everyone").is(true));
		Permission permission = mongoTemplate.findOne(query, Permission.class);
		if (permission != null && permission.getPatterns() != null) {
			return permission.getPatterns();
		}
		return new LinkedList<String>();
	}
}
