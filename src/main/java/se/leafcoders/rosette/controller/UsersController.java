package se.leafcoders.rosette.controller;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.UserIn;
import se.leafcoders.rosette.controller.dto.UserOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.service.PermissionService;
import se.leafcoders.rosette.persistence.service.UserService;
import se.leafcoders.rosette.service.SecurityService;

@RestController
@RequestMapping(value = "api/users", produces = "application/json")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SecurityService securityService;
    
    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.read(id, true);
    }

    @GetMapping
    public Collection<UserOut> getUsers(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "firstName").and(new Sort(Sort.Direction.ASC, "lastName"));        
        return userService.toOut(userService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<UserOut> postUser(@RequestBody UserIn user) {
        return new ResponseEntity<UserOut>(userService.toOut(userService.create(user, true)), HttpStatus.CREATED);
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
