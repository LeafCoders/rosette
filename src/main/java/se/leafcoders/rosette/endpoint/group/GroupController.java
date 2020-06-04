package se.leafcoders.rosette.endpoint.group;

import java.util.Collection;

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
import se.leafcoders.rosette.endpoint.user.UserOut;
import se.leafcoders.rosette.endpoint.user.UserService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/groups", produces = "application/json")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    @GetMapping(value = "/{id}")
    public GroupOut getGroup(@PathVariable Long id) {
        return groupService.toOut(groupService.read(id, true));
    }

    @GetMapping
    public Collection<GroupOut> getGroups(HttpServletRequest request) {
        Sort sort = Sort.by("name").ascending();
        return groupService.toOut(groupService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupOut postGroup(@RequestBody GroupIn group) {
        return groupService.toOut(groupService.create(group, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public GroupOut putGroup(@PathVariable Long id, HttpServletRequest request) {
        return groupService.toOut(groupService.update(id, GroupIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long id) {
        groupService.delete(id, true);
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
