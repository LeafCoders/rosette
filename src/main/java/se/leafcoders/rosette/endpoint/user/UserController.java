package se.leafcoders.rosette.endpoint.user;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.service.PermissionSumService;
import se.leafcoders.rosette.core.service.SecurityService;
import se.leafcoders.rosette.endpoint.auth.SignupUserIn;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/users", produces = "application/json")
public class UserController {

    private final UserService userService;
    private final PermissionSumService permissionSumService;
    private final SecurityService securityService;

    @GetMapping(value = "/{id}")
    public UserOut getUser(@PathVariable Long id) {
        return userService.toOut(userService.read(id, true));
    }

    @GetMapping
    public Collection<UserOut> getUsers(HttpServletRequest request) {
        Sort sort = Sort.by("firstName").ascending().and(Sort.by("lastName").ascending());
        return userService.toOut(userService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserOut postUser(@RequestBody UserIn user) {
        return userService.toOut(userService.create(user, true));
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserOut postSignupUser(@RequestBody SignupUserIn signupUser) {
        if (userService.isOkToSignupUser()) {
            return userService.toOut(userService.createSignupUser(signupUser));
        }
        throw new ForbiddenException(ApiError.UNKNOWN_REASON, "Too many calls");
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public UserOut putUser(@PathVariable Long id, HttpServletRequest request) {
        return userService.toOut(userService.update(id, UserIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id, true);
    }

    // ---

    @GetMapping(value = "/{id}/permissions")
    public List<String> getPermissionForUser(@PathVariable Long id) {
        final Long currentUser = securityService.requestUserId();
        if (id.equals(currentUser)) {
            return permissionSumService.getForUser(currentUser);
        }
        throw new ForbiddenException(ApiError.MISSING_PERMISSION, "Not your user");
    }

}
