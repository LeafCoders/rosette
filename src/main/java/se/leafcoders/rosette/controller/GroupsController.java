package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public GroupOut getGroup(@PathVariable Long id) {
        return groupService.toOut(groupService.read(id, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<GroupOut> getGroups(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");        
        return groupService.toOut(groupService.readMany(sort, true));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<GroupOut> postGroup(@RequestBody GroupIn group) {
        return new ResponseEntity<GroupOut>(groupService.toOut(groupService.create(group, true)), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public GroupOut putGroup(@PathVariable Long id, HttpServletRequest request) {
        return groupService.toOut(groupService.update(id, GroupIn.class, request, true));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        return groupService.delete(id, true);
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
    public Collection<UserOut> getUsersOfGroup(@PathVariable Long id) {
        return userService.toOut(groupService.getUsers(id));
    }

    @RequestMapping(value = "/{id}/users/{userId}", method = RequestMethod.POST, consumes = "application/json")
    public Collection<UserOut> addUserToGroup(@PathVariable Long id, @PathVariable Long userId) {
        return userService.toOut(groupService.addUser(id, userId));
    }

    @RequestMapping(value = "/{id}/users/{userId}", method = RequestMethod.DELETE)
    public Collection<UserOut> removeUserFromGroup(@PathVariable Long id, @PathVariable Long userId) {
        return userService.toOut(groupService.removeUser(id, userId));
    }
}
