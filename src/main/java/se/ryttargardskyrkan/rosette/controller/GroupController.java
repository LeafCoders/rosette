package se.ryttargardskyrkan.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.security.MongoRealm;
import se.ryttargardskyrkan.rosette.service.GroupService;

@Controller
public class GroupController extends AbstractController {
    @Autowired
    private GroupService groupService;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoRealm mongoRealm;

	@RequestMapping(value = "groups/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Group getGroup(@PathVariable String id) {
		return groupService.read(id);
	}

	@RequestMapping(value = "groups", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Group> getGroups(HttpServletResponse response) {
		return groupService.readMany(new Query().with(new Sort(Sort.Direction.ASC, "name")));
	}

	@RequestMapping(value = "groups", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Group postGroup(@RequestBody Group group, HttpServletResponse response) {
		return groupService.create(group, response);
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putGroup(@PathVariable String id, @RequestBody Group group, HttpServletResponse response) {
		Update update = new Update();
		if (group.getName() != null) {
			update.set("name", group.getName());
		}
		update.set("description", group.getDescription());
		
		groupService.update(id, group, update, response);
		
		// TODO: Change Permission to use ObjectReference to group
		// Updating groupName in permissions
		Group groupInDatabase = mongoTemplate.findById(id, Group.class);
		Update permissionUpdate = new Update();
		permissionUpdate.set("groupName", groupInDatabase.getName());
		mongoTemplate.updateMulti(Query.query(Criteria.where("groupId").is(id)), permissionUpdate, Permission.class);
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		groupService.delete(id, response);

		// Removing permissions for the group
		mongoTemplate.findAndRemove(Query.query(Criteria.where("groupId").is(id)), Permission.class);
		
		// Removing group memberships with the group that is about to be deleted
		mongoTemplate.findAndRemove(Query.query(Criteria.where("groupId").is(id)), GroupMembership.class);
		
		// Clearing auth cache
		mongoRealm.clearCache(null);
	}
}
