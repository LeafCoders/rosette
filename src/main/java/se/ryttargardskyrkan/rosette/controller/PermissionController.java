package se.ryttargardskyrkan.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.service.PermissionService;
import se.ryttargardskyrkan.rosette.service.SecurityService;

@Controller
public class PermissionController extends AbstractController {
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private SecurityService security;

	@RequestMapping(value = "permissions/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Permission getPermission(@PathVariable String id) {
		return permissionService.read(id);
	}

	@RequestMapping(value = "permissions", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Permission> getPermissions(HttpServletResponse response) {
		Query query = new Query().with(new Sort(
				new Sort.Order(Sort.Direction.ASC, "group.idRef"),
				new Sort.Order(Sort.Direction.ASC, "user.idRef")));
		return permissionService.readMany(query);
	}

	@RequestMapping(value = "permissions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Permission postPermission(@RequestBody Permission permission, HttpServletResponse response) {
		return permissionService.create(permission, response);
	}

	@RequestMapping(value = "permissions/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putPermission(@PathVariable String id, @RequestBody Permission permission, HttpServletResponse response) {
		Update update = new Update();
		if (permission.getPatterns() != null) {
			update.set("patterns", permission.getPatterns());
		}

		permissionService.update(id, permission, update, response);
	}

	@RequestMapping(value = "permissions/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deletePermission(@PathVariable String id, HttpServletResponse response) {
		permissionService.delete(id, response);
	}
	
	@RequestMapping(value = "permissionsForUser", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getPermissionForUser() {
		return permissionService.getForUser(security.requestUserId());
	}
}
