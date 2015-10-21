package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.GroupMembership;
import se.leafcoders.rosette.model.Permission;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.service.GroupMembershipService;
import se.leafcoders.rosette.service.SecurityService;
import se.leafcoders.rosette.service.UserService;
import util.QueryId;

@Controller
public class UserController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SecurityService securityService;
	@Autowired
	private UserService userService;
	@Autowired
	private GroupMembershipService groupMembershipService;

	@RequestMapping(value = "users/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable String id) {
		return userService.read(id);
	}

	@RequestMapping(value = "users", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<User> getUsers(HttpServletResponse response, @RequestParam(required = false) String groupId) {
		Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "firstName"), new Sort.Order(Sort.Direction.ASC, "lastName")));

        if (groupId != null) {
        	List<String> userIds = groupMembershipService.getUserIdsInGroup(groupId);
        	query.addCriteria(Criteria.where("id").in(userIds));
        }
        return userService.readMany(query);
	}

	@RequestMapping(value = "users", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public User postUser(@RequestBody User user, HttpServletResponse response) {
		if (user.getPassword() != null) {
			String hashedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
			user.setHashedPassword(hashedPassword);
		} else {
			user.setHashedPassword(null);
		}
		user.setPassword(null);
		return userService.create(user, response);
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putUser(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		userService.update(id, request, response);
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUser(@PathVariable String id, HttpServletResponse response) {
		securityService.checkPermission(new PermissionValue(PermissionType.USERS, PermissionAction.DELETE, id));

		User user = mongoTemplate.findById(id, User.class);
		if (user == null) {
			throw new NotFoundException();
		} else {
			// Removing permissions for the user
			mongoTemplate.findAndRemove(Query.query(Criteria.where("user.id").is(QueryId.get(id))), Permission.class);
			
			// Removing group memberships with the user that is about to be deleted
			mongoTemplate.findAndRemove(Query.query(Criteria.where("user.id").is(QueryId.get(id))), GroupMembership.class);

			// Deleting the user
			User deletedUser = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(QueryId.get(id))), User.class);
			if (deletedUser == null) {
				throw new NotFoundException();
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
			
			// Clearing auth cache
			securityService.resetPermissionCache();
		}
	}
}
