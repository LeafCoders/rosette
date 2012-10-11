package se.ryttargardskyrkan.rosette.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.NotFoundException;
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
		User user = mongoTemplate.findById(id, User.class);
		if (user == null) {
			throw new NotFoundException();
		}
		return user;
	}

	@RequestMapping(value = "users", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<User> getUsers(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer per_page,
			HttpServletResponse response) {
		Query query = new Query();
		query.sort().on("title", Order.ASCENDING);
		
		int thePage = 1;
		int thePerPage = 20;
		
		if (page != null && page > 0) {
			thePage = page;
		}
		if (per_page != null && per_page > 0) {
			thePerPage = per_page;
		}		
		
		query.limit(thePerPage);
		query.skip((thePage - 1) * thePerPage);
				
		List<User> users = mongoTemplate.find(query, User.class);

		// Header links
		Query queryForLinks = new Query();
		long numberOfUsers = mongoTemplate.count(queryForLinks, User.class);

		if (numberOfUsers > 0) {
			StringBuilder sb = new StringBuilder();
			String delimiter = "";

			if (thePage - 1 > 0) {
				sb.append(delimiter);
				sb.append("<users?page=" + (thePage - 1));
				if (per_page != null) {
					sb.append("&per_page=" + per_page);
				}

				sb.append(">; rel=\"previous\"");
				delimiter = ",";
			}

			if (numberOfUsers > thePage * thePerPage) {
				sb.append(delimiter);
				sb.append("<users?page=" + (thePage + 1));
				if (per_page != null) {
					sb.append("&per_page=" + per_page);
				}
				sb.append(">; rel=\"next\"");
				delimiter = ",";
			}

			response.setHeader("Link", sb.toString());
		}

		return users;
	}

	@RequestMapping(value = "users", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public User postUser(@RequestBody User user, HttpServletResponse response) {
//		checkPermission("users:create");
		validate(user);
		
		PasswordService passwordService = new DefaultPasswordService();
		String hashedPassword = passwordService.encryptPassword(user.getPassword());
		user.setHashedPassword(hashedPassword);
		user.setPassword(null);
		user.setStatus("active");

		mongoTemplate.insert(user);

		response.setStatus(HttpStatus.CREATED.value());
		return user;
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putUser(@PathVariable String id, @RequestBody User user, HttpServletResponse response) {
//		checkPermission("users:update");
		validate(user);

		Update update = new Update();
		if (user.getUsername() != null)
			update.set("username", user.getUsername());
		if (user.getFirstName() != null)
			update.set("firstName", user.getFirstName());

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, User.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "users/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteUser(@PathVariable String id, HttpServletResponse response) {
//		checkPermission("users:delete:" + id);

		User deletedUser = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), User.class);
		if (deletedUser == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}
	}
}
