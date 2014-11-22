package se.ryttargardskyrkan.rosette.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import se.ryttargardskyrkan.rosette.security.MongoRealm;

@Service
public class GroupMembershipService extends MongoTemplateCRUD<GroupMembership> {

	@Autowired
	private MongoRealm mongoRealm;
	
	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	public GroupMembershipService() {
		super("groupMemberships", GroupMembership.class);
	}

	@Override
	public GroupMembership create(GroupMembership data, HttpServletResponse response) {
        if (membershipExist(data)) {
            throw new SimpleValidationException(new ValidationError("groupMembership", "groupMembership.alreadyExists"));
        }

        mongoRealm.clearCache(null);
		return super.create(data, response);
	}

	@Override
	public void update(String id, GroupMembership data, Update update, HttpServletResponse response) {
        if (membershipExist(data)) {
            throw new SimpleValidationException(new ValidationError("groupMembership", "groupMembership.alreadyExists"));
        }

		mongoRealm.clearCache(null);
		super.update(id, data, update, response);
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		mongoRealm.clearCache(null);
		super.delete(id, response);
	}

	@Override
	public void insertDependencies(GroupMembership data) {
		final ObjectReference<User> userRef = data.getUser(); 
		if (userRef != null) {
			userRef.setReferredObject(userService.readNoDep(userRef.getIdRef()));
		}
		final ObjectReference<Group> groupRef = data.getGroup(); 
		if (groupRef != null) {
			groupRef.setReferredObject(groupService.readNoDep(groupRef.getIdRef()));
		}
	}

	private boolean membershipExist(GroupMembership groupMembership) {
        long count = mongoTemplate.count(Query.query(Criteria
        		.where("user.idRef").is(groupMembership.getUser().getIdRef())
        		.and("group.idRef").is(groupMembership.getGroup().getIdRef())), GroupMembership.class);
        return count > 0;
	}

	public List<String> getUserIdsInGroup(String groupId) {
		List<GroupMembership> groupMemberships = mongoTemplate.find(
        		Query.query(Criteria.where("group.idRef").is(groupId)), GroupMembership.class);
		ArrayList<String> result = new ArrayList<String>(groupMemberships.size());
		if (groupMemberships != null) {
			for (GroupMembership gm : groupMemberships) {
				result.add(gm.getUser().getIdRef());
			}
		}
		return result;		
	}
}
