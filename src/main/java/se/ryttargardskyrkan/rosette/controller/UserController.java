package se.ryttargardskyrkan.rosette.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.Theme;
import se.ryttargardskyrkan.rosette.model.User;

@Controller
public class UserController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public UserController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable String id) {
		checkPermission("users:read:" + id);
		
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
		query.sort().on("username", Order.ASCENDING);

		List<User> usersInDatabase = mongoTemplate.find(query, User.class);
		List<User> users = new ArrayList<User>();
		if (usersInDatabase != null) {
			for (User userInDatabase : usersInDatabase) {
				if (isPermitted("themes:read:" + userInDatabase.getId())) {
					users.add(userInDatabase);
				}
			}
		}

		return users;
	}

	@RequestMapping(value = "users", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public User postUser(@RequestBody User user, HttpServletResponse response) {
		checkPermission("users:create");
		validate(user);

		String hashedPassword = new DefaultPasswordService().encryptPassword(user.getPassword());
		user.setHashedPassword(hashedPassword);
		user.setPassword(null);
		user.setStatus("active");

		mongoTemplate.insert(user);

		response.setStatus(HttpStatus.CREATED.value());
		return user;
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putUser(@PathVariable String id, @RequestBody User user, HttpServletResponse response) {
		checkPermission("users:update:" + id);
		validate(user);

		Update update = new Update();
		update.set("username", user.getUsername());
		update.set("firstName", user.getFirstName());
		update.set("lastName", user.getLastName());

		if (user.getPassword() != null && !"".equals(user.getPassword().trim())) {
			String hashedPassword = new DefaultPasswordService().encryptPassword(user.getPassword());
			update.set("hashedPassword", hashedPassword);
		}

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, User.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUser(@PathVariable String id, HttpServletResponse response) {
		checkPermission("users:delete:" + id);

		User user = mongoTemplate.findById(id, User.class);
		if (user == null) {
			throw new NotFoundException();
		} else {
			// Removing group memberships with the user that is about to be deleted
			mongoTemplate.findAndRemove(Query.query(Criteria.where("userId").is(id)), GroupMembership.class);
						
			// Deleting the user
			User deletedUser = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), User.class);
			if (deletedUser == null) {
				throw new NotFoundException();
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
		}
	}
}
