package se.leafcoders.rosette.endpoint.group;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.user.User;
import se.leafcoders.rosette.endpoint.user.UserRefOut;
import se.leafcoders.rosette.endpoint.user.UserService;

@Service
public class GroupService extends PersistenceService<Group, GroupIn, GroupOut> {

    @Autowired
    UserService userService;

    public GroupService(GroupRepository repository) {
        super(Group.class, GroupPermissionValue::new, repository);
    }

    @NonNull
    private GroupRepository repo() {
        return (GroupRepository) repository;
    }

    @Override
    protected Group convertFromInDTO(GroupIn dto, JsonNode rawIn, Group item) {
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        return item;
    }

    @Override
    protected GroupOut convertToOutDTO(Group item) {
        GroupOut dto = new GroupOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setUsers(item.getUsers().stream().map(user -> new UserRefOut(user)).collect(Collectors.toList()));
        return dto;
    }

    public List<User> getUsers(Long groupId) {
        return read(groupId, true).getUsers();
    }

    public List<User> addUser(Long groupId, Long userId) {
        checkPermission(new GroupPermissionValue().update().forId(groupId));
        if (repo().isUserInGroup(userId, groupId)) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
        Group group = read(groupId, true);
        User user = userService.read(userId, true);
        group.addUser(user);
        return repository.save(group).getUsers();
    }

    public List<User> removeUser(Long groupId, Long userId) {
        checkPermission(new GroupPermissionValue().update().forId(groupId));
        Group group = read(groupId, true);
        User user = userService.read(userId, true);
        group.removeUser(user);
        return repo().save(group).getUsers();
    }
}
