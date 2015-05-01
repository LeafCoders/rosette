package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
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
import se.leafcoders.rosette.security.MongoRealm;
import se.leafcoders.rosette.security.RosettePasswordService;
import se.leafcoders.rosette.service.GroupMembershipService;
import se.leafcoders.rosette.service.SecurityService;
import se.leafcoders.rosette.service.UserService;
import util.QueryId;

@Controller
public class UserController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoRealm mongoRealm;
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
		String hashedPassword = new RosettePasswordService().encryptPassword(user.getPassword());
		user.setHashedPassword(hashedPassword);
		user.setPassword(null);
		return userService.create(user, response);
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putUser(@PathVariable String id, @RequestBody User user, HttpServletResponse response) {
		userService.update(id, user, response);
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUser(@PathVariable String id, HttpServletResponse response) {
		checkPermission("delete:users:" + id);

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
			mongoRealm.clearCache(new SimplePrincipalCollection(id, "mongoRealm"));
		}
	}
}
