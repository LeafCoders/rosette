package se.leafcoders.rosette.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.model.GroupMembership;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.util.QueryId;

@Service
public class GroupMembershipService extends MongoTemplateCRUD<GroupMembership> {

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	public GroupMembershipService() {
		super(GroupMembership.class, PermissionType.GROUP_MEMBERSHIPS);
	}

	@Override
	public GroupMembership create(GroupMembership data, HttpServletResponse response) {
        if (membershipExist(data)) {
            throw new SimpleValidationException(new ValidationError("groupMembership", "groupMembership.alreadyExists"));
        }

        security.resetPermissionCache();
		return super.create(data, response);
	}

	@Override
	public void beforeUpdate(String id, GroupMembership updateData, GroupMembership dataInDatabase) {
        if (updateData != null) {
    	    if (membershipExist(updateData)) {
                throw new SimpleValidationException(new ValidationError("groupMembership", "groupMembership.alreadyExists"));
            }
            security.resetPermissionCache();
        }
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		super.delete(id, response);
        security.resetPermissionCache();
	}

	@Override
	public void setReferences(GroupMembership data, GroupMembership dataInDb, boolean checkPermissions) {
		if (data.getUser() != null) {
			data.setUser(userService.readAsRef(data.getUser().getId(), checkPermissions));
		}
		if (data.getGroup() != null) {
			data.setGroup(groupService.read(data.getGroup().getId(), checkPermissions));
		}
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { Group.class, User.class };
    }

	private boolean membershipExist(GroupMembership groupMembership) {
        long count = mongoTemplate.count(Query.query(Criteria
        		.where("user.id").is(QueryId.get(groupMembership.getUser().getId()))
        		.and("group.id").is(groupMembership.getGroup().getId())), GroupMembership.class);
        return count > 0;
	}

	public List<GroupMembership> getForUser(User user) {
		Query query = new Query(Criteria.where("user.id").is(QueryId.get(user.getId())));
		return mongoTemplate.find(query, GroupMembership.class);
	}

	public List<GroupMembership> getForGroupIds(List<String> groupIds) {
		Query query = new Query(Criteria.where("group.id").in(groupIds));
		return mongoTemplate.find(query, GroupMembership.class);
	}

	public List<String> getUserIdsInGroup(String groupId) {
		Query query = Query.query(Criteria.where("group.id").is(groupId));
		List<GroupMembership> groupMemberships = mongoTemplate.find(query, GroupMembership.class);
		ArrayList<String> result = new ArrayList<String>(groupMemberships.size());
		if (groupMemberships != null) {
			for (GroupMembership gm : groupMemberships) {
				result.add(gm.getUser().getId());
			}
		}
		return result;		
	}
}
