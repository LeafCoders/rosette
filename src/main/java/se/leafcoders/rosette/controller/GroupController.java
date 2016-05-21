package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.service.GroupService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class GroupController extends ApiV1Controller {
    @Autowired
    private GroupService groupService;

	@RequestMapping(value = "groups/{id}", method = RequestMethod.GET, produces = "application/json")
	public Group getGroup(@PathVariable String id) {
		return groupService.read(id);
	}

	@RequestMapping(value = "groups", method = RequestMethod.GET, produces = "application/json")
	public List<Group> getGroups(HttpServletRequest request) {
		return groupService.readMany(new ManyQuery(request, "name"));
	}

	@RequestMapping(value = "groups", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Group postGroup(@RequestBody Group group, HttpServletResponse response) {
		return groupService.create(group, response);
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putGroup(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		groupService.update(id, request, response);
	}

	@RequestMapping(value = "groups/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		groupService.delete(id, response);
	}
}
