package se.leafcoders.rosette.endpoint.auth;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.service.SecurityService;
import se.leafcoders.rosette.endpoint.user.UserPermissionValue;
import se.leafcoders.rosette.endpoint.user.UserRepository;
import se.leafcoders.rosette.util.ClientServerTime;

@RequiredArgsConstructor
@RestController
public class LoginController extends AuthController {

    private final JwtAuthenticationService jwtAuthenticationService;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @PostMapping(value = "login")
    @ResponseStatus(HttpStatus.OK)
    public Object login(@RequestBody Login login, HttpServletResponse response) {
        CurrentUser userToLogin = null;
        try {
            userToLogin = currentUserService.loadUserByUsername(login.getUsername());
        } catch (UsernameNotFoundException ignore) {
            throw new ForbiddenException(ApiError.AUTH_USER_NOT_FOUND, login.getUsername());
        }

        if (userToLogin != null && userToLogin.isEnabled()
                && new BCryptPasswordEncoder().matches(login.getPassword(), userToLogin.getPassword())) {
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

    @PostMapping(value = "loginAs/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Object loginAs(@PathVariable Long userId, HttpServletResponse response) {
        securityService.checkPermission(new UserPermissionValue().loginAs().forId(userId));

        CurrentUser userToLogin = null;
        try {
            userToLogin = currentUserService.loadUserById(userId);
        } catch (UsernameNotFoundException ignore) {
            throw new ForbiddenException(ApiError.AUTH_USER_NOT_FOUND, userId.toString());
        }

        if (userToLogin != null) {
            if (userToLogin.isSuperAdmin()) {
                throw new ForbiddenException(ApiError.AUTH_USER_IS_SUPER_ADMIN, userId.toString());
            }
            jwtAuthenticationService.addAuthenticationHeader(response, new CurrentUserAuthentication(userToLogin));
            response.setStatus(HttpServletResponse.SC_OK);
            return successData(userToLogin);
        } else {
            throw new ForbiddenException(ApiError.AUTH_USER_NOT_FOUND, userId.toString());
        }
    }

    private void updateLastLoginTime(Long id) {
        userRepository.setLastLoginTime(id, ClientServerTime.serverTimeNow());
    }

    private HashMap<String, String> successData(CurrentUser user) {
        HashMap<String, String> successData = new HashMap<String, String>();
        successData.put("id", user.getId().toString());
        successData.put("fullName", user.getFullName());
        successData.put("email", user.getUsername());
        return successData;
    }
}
