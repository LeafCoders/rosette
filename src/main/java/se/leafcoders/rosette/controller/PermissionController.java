package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.Permission;
import se.leafcoders.rosette.service.PermissionService;
import se.leafcoders.rosette.service.SecurityService;

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
				new Sort.Order(Sort.Direction.ASC, "group.id"),
				new Sort.Order(Sort.Direction.ASC, "user.id")));
		return permissionService.readMany(query);
	}

	@RequestMapping(value = "permissions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Permission postPermission(@RequestBody Permission permission, HttpServletResponse response) {
		return permissionService.create(permission, response);
	}

	@RequestMapping(value = "permissions/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putPermission(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		permissionService.update(id, request, response);
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
