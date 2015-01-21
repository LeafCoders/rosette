package se.ryttargardskyrkan.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.model.*;
import se.ryttargardskyrkan.rosette.service.GroupMembershipService;

@Controller
public class GroupMembershipController extends AbstractController {
    @Autowired
    private GroupMembershipService groupMembershipService;

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public GroupMembership getGroupMembership(@PathVariable String id) {
		return groupMembershipService.read(id);
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<GroupMembership> getGroupMemberships(@RequestParam(value = "groupId", required = false) String groupId, HttpServletRequest request, HttpServletResponse response) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "group.id")));
        if (groupId != null && !groupId.isEmpty()) {
            query.addCriteria(Criteria.where("group.id").is(groupId));
        }
		return groupMembershipService.readMany(query);
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public GroupMembership postGroupMembership(@RequestBody GroupMembership groupMembership, HttpServletResponse response) {
		return groupMembershipService.create(groupMembership, response);
	}

    @RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putGroupMembership(@PathVariable String id, @RequestBody GroupMembership groupMembership, HttpServletResponse response) {
		groupMembershipService.update(id, groupMembership, response);
    }

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroupMembership(@PathVariable String id, HttpServletResponse response) {
		groupMembershipService.delete(id, response);
	}
}
