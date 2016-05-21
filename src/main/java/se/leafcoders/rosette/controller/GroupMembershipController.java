package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.GroupMembership;
import se.leafcoders.rosette.service.GroupMembershipService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class GroupMembershipController extends ApiV1Controller {
    @Autowired
    private GroupMembershipService groupMembershipService;

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.GET, produces = "application/json")
	public GroupMembership getGroupMembership(@PathVariable String id) {
		return groupMembershipService.read(id);
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.GET, produces = "application/json")
	public List<GroupMembership> getGroupMemberships(HttpServletRequest request, @RequestParam(value = "groupId", required = false) String groupId) {
	    ManyQuery manyQuery = new ManyQuery(request, "group.id");
        if (groupId != null && !groupId.isEmpty()) {
            manyQuery.addCriteria(Criteria.where("group.id").is(groupId));
        }
		return groupMembershipService.readMany(manyQuery);
	}

	@RequestMapping(value = "groupMemberships", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public GroupMembership postGroupMembership(@RequestBody GroupMembership groupMembership, HttpServletResponse response) {
		return groupMembershipService.create(groupMembership, response);
	}

    @RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putGroupMembership(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		groupMembershipService.update(id, request, response);
    }

	@RequestMapping(value = "groupMemberships/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroupMembership(@PathVariable String id, HttpServletResponse response) {
		groupMembershipService.delete(id, response);
	}
}
