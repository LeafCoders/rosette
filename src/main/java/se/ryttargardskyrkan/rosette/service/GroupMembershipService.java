package se.ryttargardskyrkan.rosette.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
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
	public void update(String id, GroupMembership data, HttpServletResponse response) {
        if (membershipExist(data)) {
            throw new SimpleValidationException(new ValidationError("groupMembership", "groupMembership.alreadyExists"));
        }

		mongoRealm.clearCache(null);
		super.update(id, data, response);
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		mongoRealm.clearCache(null);
		super.delete(id, response);
	}

	@Override
	public void insertDependencies(GroupMembership data) {
		if (data.getUser() != null) {
			data.setUser(userService.read(data.getUser().getId()));
		}
		if (data.getGroup() != null) {
			data.setGroup(groupService.read(data.getGroup().getId()));
		}
	}

	private boolean membershipExist(GroupMembership groupMembership) {
        long count = mongoTemplate.count(Query.query(Criteria
        		.where("user.id").is(groupMembership.getUser().getId())
        		.and("group.id").is(groupMembership.getGroup().getId())), GroupMembership.class);
        return count > 0;
	}

	public List<String> getUserIdsInGroup(String groupId) {
		List<GroupMembership> groupMemberships = mongoTemplate.find(
        		Query.query(Criteria.where("group.id").is(groupId)), GroupMembership.class);
		ArrayList<String> result = new ArrayList<String>(groupMemberships.size());
		if (groupMemberships != null) {
			for (GroupMembership gm : groupMemberships) {
				result.add(gm.getUser().getId());
			}
		}
		return result;		
	}
}
