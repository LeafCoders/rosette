package se.leafcoders.rosette.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.controller.dto.SignupUserIn;
import se.leafcoders.rosette.controller.dto.UserIn;
import se.leafcoders.rosette.controller.dto.UserOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.persistence.service.PermissionService;
import se.leafcoders.rosette.persistence.service.UserService;
import se.leafcoders.rosette.service.SecurityService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/users", produces = "application/json")
public class UsersController {

    private final UserService userService;
    private final PermissionService permissionService;
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
    public ResponseEntity<UserOut> postUser(@RequestBody UserIn user) {
        return new ResponseEntity<UserOut>(userService.toOut(userService.create(user, true)), HttpStatus.CREATED);
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<UserOut> postSignupUser(@RequestBody SignupUserIn signupUser) {
        if (userService.isOkToSignupUser()) {
            return new ResponseEntity<UserOut>(userService.toOut(userService.createSignupUser(signupUser)),
                    HttpStatus.CREATED);
        }
        throw new ForbiddenException(ApiError.UNKNOWN_REASON, "Too many calls");
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public UserOut putUser(@PathVariable Long id, HttpServletRequest request) {
        return userService.toOut(userService.update(id, UserIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.delete(id, true);
    }

    // ---

    @GetMapping(value = "/{id}/permissions")
    public List<String> getPermissionForUser(@PathVariable Long id) {
        final Long currentUser = securityService.requestUserId();
        if (id.equals(currentUser)) {
            return permissionService.getForUser(currentUser);
        }
        throw new ForbiddenException(ApiError.MISSING_PERMISSION, "Not your user");
    }

}
