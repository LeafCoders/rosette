package se.leafcoders.rosette.controller.auth;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.converter.ClientServerTime;
import se.leafcoders.rosette.persistence.repository.UserRepository;
import se.leafcoders.rosette.service.SecurityService;

@RequiredArgsConstructor
@RestController
public class LoginController extends AuthController {

    private final JwtAuthenticationService jwtAuthenticationService;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @PostMapping(value = "login")
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
    public Object loginAs(@PathVariable Long userId, HttpServletResponse response) {
        securityService.checkPermission(PermissionType.users().loginAs().forId(userId));

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
