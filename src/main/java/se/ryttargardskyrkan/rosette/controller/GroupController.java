package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
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
import se.ryttargardskyrkan.rosette.model.Event;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.GroupMembership;

@Controller
public class GroupController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public GroupController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Group getGroup(@PathVariable String id) {
		checkPermission("groups:read:" + id);
		
		Group group = mongoTemplate.findById(id, Group.class);
		if (group == null) {
			throw new NotFoundException();
		}
		return group;
	}

	@RequestMapping(value = "groups", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Group> getGroups(HttpServletResponse response) {
		Query query = new Query();
		query.sort().on("groupname", Order.ASCENDING);

		List<Group> groupsInDatabase = mongoTemplate.find(query, Group.class);
		List<Group> groups = new ArrayList<Group>();
		if (groupsInDatabase != null) {
			for (Group groupInDatabase : groupsInDatabase) {
				if (isPermitted("groups:read:" + groupInDatabase.getId())) {
					groups.add(groupInDatabase);
				}
			}
		}

		return groups;
	}

	@RequestMapping(value = "groups", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Group postGroup(@RequestBody Group group, HttpServletResponse response) {
		checkPermission("groups:create");
		validate(group);
		
		mongoTemplate.insert(group);

		// Adding 'update' permission to the group itself
		List<String> permissions = new ArrayList<String>();
		permissions.add("groups:update:" + group.getId());
		group.setPermissions(permissions);
		
		Update update = Update.update("permissions", permissions);
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(group.getId())), update, Group.class);

		response.setStatus(HttpStatus.CREATED.value());
		return group;
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putGroup(@PathVariable String id, @RequestBody Group group, HttpServletResponse response) {
		checkPermission("groups:update:" + id);
		validate(group);

		Update update = new Update();
		update.set("name", group.getName());
		update.set("description", group.getDescription());
		update.set("permissions", group.getPermissions());

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Group.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		checkPermission("groups:delete:" + id);

		Group group = mongoTemplate.findById(id, Group.class);
		if (group == null) {
			throw new NotFoundException();
		} else {
			// Removing group memberships with the group that is about to be deleted
			mongoTemplate.findAndRemove(Query.query(Criteria.where("groupId").is(id)), GroupMembership.class);
			
			// Removing the group
			Group deletedGroup = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Group.class);
			if (deletedGroup == null) {
				throw new NotFoundException();
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
		}
	}
}
