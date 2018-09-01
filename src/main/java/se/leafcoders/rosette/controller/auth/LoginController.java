package se.leafcoders.rosette.controller.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.persistence.repository.UserRepository;

@RestController
public class LoginController extends AuthController {

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserRepository userRepository;

	@PostMapping(value = "login")
	public Object login(@RequestBody Login login, HttpServletResponse response) {
        CurrentUser userToLogin = null;
        try {
            userToLogin = currentUserService.loadUserByUsername(login.getUsername());
        } catch (UsernameNotFoundException ignore) {
            throw new ForbiddenException(ApiError.AUTH_USER_NOT_FOUND, login.getUsername());
        }

        if (userToLogin != null && userToLogin.isEnabled() && new BCryptPasswordEncoder().matches(login.getPassword(), userToLogin.getPassword())) {
            updateLastLoginTime(userToLogin.getId());
            jwtAuthenticationService.addAuthenticationHeader(response, new CurrentUserAuthentication(userToLogin));
            response.setStatus(HttpServletResponse.SC_OK);
            return successData(userToLogin);
        } else {
            if (userToLogin.isEnabled()) {
                throw new ForbiddenException(ApiError.AUTH_INCORRECT_PASSWORD);
            }
            throw new ForbiddenException(ApiError.AUTH_USER_NOT_ACTIVATED);
        }
	}

	private void updateLastLoginTime(Long id) {
	    userRepository.setLastLoginTime(id, LocalDateTime.now());
	}

	private HashMap<String, String> successData(CurrentUser user) {
        HashMap<String, String> successData = new HashMap<String, String>();
        successData.put("id", user.getId().toString());
        successData.put("fullName", user.getFullName());
        successData.put("email", user.getUsername());
        return successData;
	}
}
