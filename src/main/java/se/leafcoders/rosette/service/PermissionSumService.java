package se.leafcoders.rosette.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.model.Permission;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.PermissionRepository;
import se.leafcoders.rosette.persistence.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class PermissionSumService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public List<String> getForUser(Long userId) {
        List<String> permissionStrings = new LinkedList<String>();

        // Adding permissions for everyone (implicit also from Public)
        permissionStrings.addAll(getForEveryone());

        // Adding permissions for specified user
        User user = userId != null ? userRepository.findById(userId).get() : null;
        if (user != null) {
            permissionStrings.addAll(getPermissionsForUser(user));
            permissionStrings.addAll(getPermissionsForGroups(user));
        }
        return permissionStrings;
    }

    public List<String> getForEveryone() {
        List<String> permissionStrings = new LinkedList<String>();

        // Adding permissions for public
        permissionStrings.addAll(getForPublic());

        List<Permission> permissions = permissionRepository.findByLevel(Permission.LEVEL_ALL_USERS);
        if (permissions != null) {
            permissionStrings.addAll(permissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream)
                    .collect(Collectors.toList()));
        }
        return permissionStrings;
    }

    public List<String> getForPublic() {
        List<Permission> permissions = permissionRepository.findByLevel(Permission.LEVEL_PUBLIC);
        if (permissions != null) {
            return permissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream).collect(Collectors.toList());
        }
        return new LinkedList<String>();
    }

    private List<String> getPermissionsForUser(User user) {
        List<String> permissions = new ArrayList<String>();

        // Add read/update permissions for own user
        permissions.add(PermissionType.users().read().forPersistable(user).toString());
        permissions.add(PermissionType.users().update().forPersistable(user).toString());

        // Adding permissions specific for this user
        List<Permission> userPermissions = permissionRepository.findByLevelAndEntityId(Permission.LEVEL_USER,
                user.getId());
        if (userPermissions != null) {
            permissions.addAll(userPermissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream)
                    .collect(Collectors.toList()));
        }
        return permissions;
    }

    private List<String> getPermissionsForGroups(User user) {
        List<String> permissions = new ArrayList<String>();

        // Adding permissions from group where user is member
        List<Group> groups = user.getGroups();
        if (groups != null && !groups.isEmpty()) {
            // Adding permissions specific for each group
            List<Permission> groupPermissions = permissionRepository.findByLevelAndEntityIdIn(Permission.LEVEL_GROUP,
                    groups.stream().map(g -> g.getId()).collect(Collectors.toList()));
            if (groupPermissions != null) {
                permissions.addAll(groupPermissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream)
                        .collect(Collectors.toList()));
            }

            // Add read permission for each user in groups
            List<User> usersInGroups = userRepository.findUsersInGroups(groups);
            if (usersInGroups != null && !usersInGroups.isEmpty()) {
                permissions.addAll(
                        usersInGroups.stream().map(u -> PermissionType.users().read().forPersistable(user).toString())
                                .collect(Collectors.toList()));
            }
        }
        return permissions;
    }

}
