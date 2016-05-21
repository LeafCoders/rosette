package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.service.GroupMembershipService;
import se.leafcoders.rosette.service.UserService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class UserController extends ApiV1Controller {
	@Autowired
	private UserService userService;
	@Autowired
	private GroupMembershipService groupMembershipService;

	@RequestMapping(value = "users/{id}", method = RequestMethod.GET, produces = "application/json")
	public User getUser(@PathVariable String id) {
		return userService.read(id);
	}

	@RequestMapping(value = "users", method = RequestMethod.GET, produces = "application/json")
	public List<User> getUsers(HttpServletRequest request, @RequestParam(required = false) String groupId) {
	    ManyQuery manyQuery = new ManyQuery(request, "firstName,lastName");
        if (groupId != null) {
        	List<String> userIds = groupMembershipService.getUserIdsInGroup(groupId);
        	manyQuery.addCriteria(Criteria.where("id").in(userIds));
        }
        return userService.readMany(manyQuery);
	}

	@RequestMapping(value = "users", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
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
        userService.delete(id, response);
	}
}
