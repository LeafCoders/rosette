package se.ryttargardskyrkan.rosette.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.User;

@Service
public class GroupService extends MongoTemplateCRUD<Group> {

	@Autowired
	ResourceTypeService resourceTypeService;
	@Autowired
	GroupMembershipService groupMembershipService;
	
	public GroupService() {
		super("groups", Group.class);
	}

	@Override
	public Group create(Group data, HttpServletResponse response) {
		validateUniqueId(data);
		return super.create(data, response);
	}

	@Override
	public void insertDependencies(Group data) {
	}
	
	public boolean containsUsers(String groupId, List<ObjectReference<User>> userIdRefs) {
		List<String> userIdsInGroup = groupMembershipService.getUserIdsInGroup(groupId);
		for (ObjectReference<User> userIdRef : userIdRefs) {
			if (!userIdsInGroup.contains(userIdRef.getIdRef())) {
				return false;
			}
		}
		return true;
	}
}
