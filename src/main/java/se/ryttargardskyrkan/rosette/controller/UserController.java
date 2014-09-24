package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import se.ryttargardskyrkan.rosette.security.MongoRealm;
import se.ryttargardskyrkan.rosette.security.RosettePasswordService;
import se.ryttargardskyrkan.rosette.service.SecurityService;

@Controller
public class UserController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoRealm mongoRealm;
	@Autowired
	private SecurityService security;

	@RequestMapping(value = "users/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable String id) {
		checkPermission("read:users:" + id);
		
		User user = mongoTemplate.findById(id, User.class);
		if (user == null) {
			throw new NotFoundException();
		}
		return user;
	}

	@RequestMapping(value = "users", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<User> getUsers(HttpServletResponse response) {
		Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "firstName"), new Sort.Order(Sort.Direction.ASC, "lastName")));

		List<User> usersInDatabase = mongoTemplate.find(query, User.class);
		List<User> users = new ArrayList<User>();
		if (usersInDatabase != null) {
			for (User userInDatabase : usersInDatabase) {
				if (isPermitted("read:users:" + userInDatabase.getId())) {
					users.add(userInDatabase);
				}
			}
		}

		return users;
	}

	@RequestMapping(value = "users", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public User postUser(@RequestBody User user, HttpServletResponse response) {
		checkPermission("create:users");
		security.validate(user);

		long count = mongoTemplate.count(Query.query(Criteria.where("username").is(user.getUsername())), User.class);
		if (count > 0) {
			throw new SimpleValidationException(new ValidationError("username", "user.username.duplicatedUsernameNotAllowed"));
		} else {
			String hashedPassword = new RosettePasswordService().encryptPassword(user.getPassword());
			user.setHashedPassword(hashedPassword);
			user.setPassword(null);
			user.setStatus("active");

			mongoTemplate.insert(user);

			response.setStatus(HttpStatus.CREATED.value());
			return user;
		}
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putUser(@PathVariable String id, @RequestBody User user, HttpServletResponse response) {
		checkPermission("update:users:" + id);
		security.validate(user);

		Update update = new Update();
		if (user.getUsername() != null)
			update.set("username", user.getUsername());
		if (user.getFirstName() != null)
			update.set("firstName", user.getFirstName());
		if (user.getLastName() != null)
			update.set("lastName", user.getLastName());

		if (user.getPassword() != null && !"".equals(user.getPassword().trim())) {
			String hashedPassword = new RosettePasswordService().encryptPassword(user.getPassword());
			update.set("hashedPassword", hashedPassword);
		}

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, User.class).getN() == 0) {
			throw new NotFoundException();
		}
		
		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUser(@PathVariable String id, HttpServletResponse response) {
		checkPermission("delete:users:" + id);

		User user = mongoTemplate.findById(id, User.class);
		if (user == null) {
			throw new NotFoundException();
		} else {
			// Removing permissions for the user
			mongoTemplate.findAndRemove(Query.query(Criteria.where("user.idRef").is(id)), Permission.class);
			
			// Removing group memberships with the user that is about to be deleted
			mongoTemplate.findAndRemove(Query.query(Criteria.where("user.idRef").is(id)), GroupMembership.class);

			// Deleting the user
			User deletedUser = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), User.class);
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
