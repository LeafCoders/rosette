package se.leafcoders.rosette.controller.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.model.error.ValidationError;

@RestController
public class LoginController extends AuthController {

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    private CurrentUserService currentUserService;

	@RequestMapping(value = "login", method = RequestMethod.POST, consumes = "application/json")
	public Object createForgottenPassword(
        @RequestParam(value="username", required=false) String username,
        @RequestParam(value="password", required=false) String password,
        HttpServletResponse response
    ) {
        if (username == null) {
            throwValidationError("username", "Must be specified as url parameter");
        }
        if (password == null) {
            throwValidationError("password", "Must be specified as url parameter");
        }

        CurrentUser userToLogin = null;
        try {
            userToLogin = currentUserService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ignore) {
            throwValidationError("username", "User with username not found");
        }

        if (userToLogin != null && new BCryptPasswordEncoder().matches(password, userToLogin.getPassword())) {
            jwtAuthenticationService.addAuthenticationHeader(response, new CurrentUserAuthentication(userToLogin));
            response.setStatus(HttpServletResponse.SC_OK);
            return successData(userToLogin);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("password", "Invalid password"));
            return errors;
        }
	}

	private HashMap<String, String> successData(CurrentUser user) {
        HashMap<String, String> successData = new HashMap<String, String>();
        successData.put("id", user.getId());
        successData.put("fullName", user.getFullName());
        successData.put("email", user.getUsername());
        return successData;
	}
}
