package se.leafcoders.rosette.persistence.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.GroupIn;
import se.leafcoders.rosette.controller.dto.GroupOut;
import se.leafcoders.rosette.controller.dto.UserRefOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.GroupRepository;

@Service
public class GroupService extends PersistenceService<Group, GroupIn, GroupOut> {

    @Autowired
    UserService userService;

    public GroupService(GroupRepository repository) {
        super(Group.class, PermissionType::groups, repository);
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
        checkPermission(PermissionType.groups().update().forId(groupId));
        Group group = read(groupId, true);
        User user = userService.read(userId, true);
        group.addUser(user);
        try {
            return repository.save(group).getUsers();
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<User> removeUser(Long groupId, Long userId) {
        checkPermission(PermissionType.groups().update().forId(groupId));
        Group group = read(groupId, true);
        User user = userService.read(userId, true);
        group.removeUser(user);
        return repository.save(group).getUsers();
    }
}
