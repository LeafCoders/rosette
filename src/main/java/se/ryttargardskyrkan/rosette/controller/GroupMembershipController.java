package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.security.MongoRealm;

@Controller
public class GroupMembershipController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoRealm mongoRealm;

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public GroupMembership getGroupMembership(@PathVariable String id) {
		checkPermission("groupMemberships:read:" + id);

		GroupMembership groupMembership = mongoTemplate.findById(id, GroupMembership.class);
		if (groupMembership == null) {
			throw new NotFoundException();
		} else {
            List<User> users = mongoTemplate.findAll(User.class);
            List<Group> groups = mongoTemplate.findAll(Group.class);

            for (Group group : groups) {
                if (group.getId().equals(groupMembership.getGroupId())) {
                    groupMembership.setGroupName(group.getName());
                    break;
                }
            }
            for (User user : users) {
                if (user.getId().equals(groupMembership.getUserId())) {
                    groupMembership.setUsername(user.getUsername());
                    break;
                }
            }
        }
		return groupMembership;
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<GroupMembership> getGroupMemberships(HttpServletResponse response) {
		List<GroupMembership> groupMemberships = new ArrayList<GroupMembership>();

		List<GroupMembership> groupMembershipsInDatabase = mongoTemplate.findAll(GroupMembership.class);
		if (groupMembershipsInDatabase != null) {

            List<User> users = mongoTemplate.findAll(User.class);
            List<Group> groups = mongoTemplate.findAll(Group.class);

			for (GroupMembership groupMembershipInDatabase : groupMembershipsInDatabase) {
				if (isPermitted("groupMemberships:read:" + groupMembershipInDatabase.getId())) {
                    for (Group group : groups) {
                        if (group.getId().equals(groupMembershipInDatabase.getGroupId())) {
                            groupMembershipInDatabase.setGroupName(group.getName());
                            break;
                        }
                    }
                    for (User user : users) {
                        if (user.getId().equals(groupMembershipInDatabase.getUserId())) {
                            groupMembershipInDatabase.setUsername(user.getUsername());
                            break;
                        }
                    }

					groupMemberships.add(groupMembershipInDatabase);
				}
			}
		}

		return groupMemberships;
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public GroupMembership postGroupMembership(@RequestBody GroupMembership groupMembership, HttpServletResponse response) {
		checkPermission("groupMemberships:create");
		validate(groupMembership);

		mongoTemplate.insert(groupMembership);

		// Clearing auth cache
		mongoRealm.clearCache(new SimplePrincipalCollection(groupMembership.getUserId(), "mongoRealm"));

		response.setStatus(HttpStatus.CREATED.value());

		// Clearing auth cache
		mongoRealm.clearCache(null);

		return groupMembership;
	}

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroupMembership(@PathVariable String id, HttpServletResponse response) {
		checkPermission("groupMemberships:delete:" + id);

		GroupMembership deletedGroupMembership = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), GroupMembership.class);
		if (deletedGroupMembership == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}

		// Clearing auth cache
		mongoRealm.clearCache(new SimplePrincipalCollection(id, "mongoRealm"));
	}
}
