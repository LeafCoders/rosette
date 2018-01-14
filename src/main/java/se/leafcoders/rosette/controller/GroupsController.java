package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
import se.leafcoders.rosette.controller.dto.GroupIn;
import se.leafcoders.rosette.controller.dto.GroupOut;
import se.leafcoders.rosette.controller.dto.UserOut;
import se.leafcoders.rosette.persistence.service.GroupService;
import se.leafcoders.rosette.persistence.service.UserService;

@RestController
@RequestMapping(value = "api/groups", produces = "application/json")
public class GroupsController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/{id}")
    public GroupOut getGroup(@PathVariable Long id) {
        return groupService.toOut(groupService.read(id, true));
    }

    @GetMapping
    public Collection<GroupOut> getGroups(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");        
        return groupService.toOut(groupService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<GroupOut> postGroup(@RequestBody GroupIn group) {
        return new ResponseEntity<GroupOut>(groupService.toOut(groupService.create(group, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public GroupOut putGroup(@PathVariable Long id, HttpServletRequest request) {
        return groupService.toOut(groupService.update(id, GroupIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        return groupService.delete(id, true);
    }

    @GetMapping(value = "/{id}/users")
    public Collection<UserOut> getUsersOfGroup(@PathVariable Long id) {
        return userService.toOut(groupService.getUsers(id));
    }

    @PostMapping(value = "/{id}/users/{userId}", consumes = "application/json")
    public Collection<UserOut> addUserToGroup(@PathVariable Long id, @PathVariable Long userId) {
        return userService.toOut(groupService.addUser(id, userId));
    }

    @DeleteMapping(value = "/{id}/users/{userId}")
    public Collection<UserOut> removeUserFromGroup(@PathVariable Long id, @PathVariable Long userId) {
        return userService.toOut(groupService.removeUser(id, userId));
    }
}
