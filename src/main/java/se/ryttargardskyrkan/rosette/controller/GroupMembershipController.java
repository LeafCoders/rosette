package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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

@Controller
public class GroupMembershipController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public GroupMembershipController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public GroupMembership getGroupMembership(@PathVariable String id) {
		checkPermission("groupMemberships:read:" + id);
		
		GroupMembership groupMembership = mongoTemplate.findById(id, GroupMembership.class);
		if (groupMembership == null) {
			throw new NotFoundException();
		}
		return groupMembership;
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<GroupMembership> getGroupMemberships(HttpServletResponse response) {
		List<GroupMembership> groupMemberships = new ArrayList<GroupMembership>();
		
		List<GroupMembership> groupMembershipsInDatabase = mongoTemplate.findAll(GroupMembership.class);
		if (groupMembershipsInDatabase != null) {
			for (GroupMembership groupMembershipInDatabase : groupMembershipsInDatabase) {
				if (isPermitted("groupMemberships:read:" + groupMembershipInDatabase.getId())) {
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

		response.setStatus(HttpStatus.CREATED.value());
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
	}
}
