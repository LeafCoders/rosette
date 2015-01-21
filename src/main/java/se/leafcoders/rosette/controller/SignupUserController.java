package se.ryttargardskyrkan.rosette.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.ryttargardskyrkan.rosette.model.SignupUser;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.security.MongoRealm;
import se.ryttargardskyrkan.rosette.security.RosettePasswordService;
import se.ryttargardskyrkan.rosette.service.SecurityService;
import se.ryttargardskyrkan.rosette.service.SignupUserService;
import se.ryttargardskyrkan.rosette.service.UserService;

@Controller
public class SignupUserController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoRealm mongoRealm;
	@Autowired
	private SecurityService securityService;
	@Autowired
	private SignupUserService signupUserService;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "signupUsers/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public SignupUser getSignupUser(@PathVariable String id) {
		return signupUserService.read(id);
	}

	@RequestMapping(value = "signupUsers", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<SignupUser> getSignupUsers(HttpServletResponse response) {
		return signupUserService.readMany(null);
	}

	@RequestMapping(value = "signupUsers", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public SignupUser postSignupUser(@RequestBody SignupUser signupUser, HttpServletResponse response) {
		// Only allow one signup each minute
		SignupUser latestSignup = signupUserService.getLatestSignupUser();
		if (latestSignup != null && ((new Date().getTime()-latestSignup.getCreatedTime().getTime()) < 60000)) {
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			return null;
		}

		String hashedPassword = new RosettePasswordService().encryptPassword(signupUser.getPassword());
		signupUser.setHashedPassword(hashedPassword);
		signupUser.setPassword(null);

		return signupUserService.create(signupUser, response);
	}

	@RequestMapping(value = "signupUsers/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putSignupUser(@PathVariable String id, @RequestBody SignupUser signupUser, HttpServletResponse response) {
		signupUserService.update(id, signupUser, response);
	}

	@RequestMapping(value = "signupUsers/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteSignupUser(@PathVariable String id, HttpServletResponse response) {
		signupUserService.delete(id, response);
	}

	@RequestMapping(value = "signupUsersTransform/{id}", method = RequestMethod.POST, produces = "application/json")
	public void transformSignupUser(@PathVariable String id, HttpServletResponse response) {
		SignupUser signupUser = signupUserService.read(id);
		User user = new User();
		user.setUsername(signupUser.getUsername());
		user.setFirstName(signupUser.getFirstName());
		user.setLastName(signupUser.getLastName());
		user.setEmail(signupUser.getEmail());
		user.setHashedPassword(signupUser.getHashedPassword());
		userService.create(user, response);
		signupUserService.delete(id, response);
	}
}
