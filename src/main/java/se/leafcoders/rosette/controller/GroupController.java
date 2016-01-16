package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.service.GroupService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class GroupController extends AbstractController {
    @Autowired
    private GroupService groupService;

	@RequestMapping(value = "groups/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Group getGroup(@PathVariable String id) {
		return groupService.read(id);
	}

	@RequestMapping(value = "groups", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Group> getGroups(HttpServletRequest request) {
		return groupService.readMany(new ManyQuery(request, "name"));
	}

	@RequestMapping(value = "groups", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
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
