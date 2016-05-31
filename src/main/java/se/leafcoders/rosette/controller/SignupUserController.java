package se.leafcoders.rosette.controller;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.SignupUser;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.service.SignupUserService;
import se.leafcoders.rosette.service.UserService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class SignupUserController extends ApiV1Controller {
	@Autowired
	private SignupUserService signupUserService;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "signupUsers/{id}", method = RequestMethod.GET, produces = "application/json")
	public SignupUser getSignupUser(@PathVariable String id) {
		return signupUserService.read(id);
	}

	@RequestMapping(value = "signupUsers", method = RequestMethod.GET, produces = "application/json")
	public List<SignupUser> getSignupUsers(HttpServletRequest request) {
		return signupUserService.readMany(new ManyQuery(request));
	}

	@RequestMapping(value = "signupUsers", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public SignupUser postSignupUser(@RequestBody SignupUser signupUser, HttpServletResponse response) {
		// Only allow one signup each minute
		SignupUser latestSignup = signupUserService.getLatestSignupUser();
		if (latestSignup != null && ((new Date().getTime()-latestSignup.getCreatedTime().getTime()) < 60000)) {
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			return null;
		}

		String password = null;
        try {
            password = new String(Base64.getUrlDecoder().decode(signupUser.getPassword()));
        } catch (IllegalArgumentException ignore) {
            throwValidationError("password", "Password must be base64 url encoded");
        }
		
		String hashedPassword = new BCryptPasswordEncoder().encode(password);
		signupUser.setHashedPassword(hashedPassword);
		signupUser.setPassword(null);
		return signupUserService.create(signupUser, response);
	}

	@RequestMapping(value = "signupUsers/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putSignupUser(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		signupUserService.update(id, request, response);
	}

	@RequestMapping(value = "signupUsers/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteSignupUser(@PathVariable String id, HttpServletResponse response) {
		signupUserService.delete(id, response);
	}

	@RequestMapping(value = "signupUsersTransform/{id}", method = RequestMethod.POST, produces = "application/json")
	public void transformSignupUser(@PathVariable String id, HttpServletResponse response) {
		SignupUser signupUser = signupUserService.read(id);
		User user = new User();
		user.setEmail(signupUser.getEmail());
		user.setFirstName(signupUser.getFirstName());
		user.setLastName(signupUser.getLastName());
		user.setHashedPassword(signupUser.getHashedPassword());
		userService.create(user, response);
		signupUserService.delete(id, response);
	}
}
