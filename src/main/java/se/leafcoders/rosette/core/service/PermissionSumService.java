package se.leafcoders.rosette.core.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.core.permission.PermissionType;
import se.leafcoders.rosette.endpoint.group.Group;
import se.leafcoders.rosette.endpoint.permission.Permission;
import se.leafcoders.rosette.endpoint.permission.PermissionRepository;
import se.leafcoders.rosette.endpoint.user.User;
import se.leafcoders.rosette.endpoint.user.UserRepository;

@RequiredArgsConstructor
@Service
public class PermissionSumService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public List<String> getForUser(Long userId) {
        final List<String> permissionStrings = new LinkedList<>();

        // Adding permissions for everyone (implicit also from Public)
        permissionStrings.addAll(getForEveryone());

        // Adding permissions for specified user
        Optional.ofNullable(userId).map(id -> userRepository.findById(userId).get()).ifPresent(user -> {
            permissionStrings.addAll(getPermissionsForUser(user));
            permissionStrings.addAll(getPermissionsForGroups(user));
        });
        return removeDuplicates(permissionStrings);
    }

    public List<String> getForEveryone() {
        final List<String> permissionStrings = new LinkedList<>();

        // Adding permissions for public
        permissionStrings.addAll(getForPublic());

        List<Permission> permissions = permissionRepository.findByLevel(Permission.LEVEL_ALL_USERS);
        if (permissions != null) {
            permissionStrings.addAll(collectPermissionPatterns(permissions));
        }
        return removeDuplicates(permissionStrings);
    }

    public List<String> getForPublic() {
        List<Permission> permissions = permissionRepository.findByLevel(Permission.LEVEL_PUBLIC);
        if (permissions != null) {
            return removeDuplicates(collectPermissionPatterns(permissions));
        }
        return new LinkedList<String>();
    }

    private List<String> getPermissionsForUser(@NonNull User user) {
        final List<String> permissions = new ArrayList<>();

        // Add read/update permissions for own user
        permissions.add(PermissionType.users().read().forPersistable(user).toString());
        permissions.add(PermissionType.users().update().forPersistable(user).toString());

        // Adding permissions specific for this user
        List<Permission> userPermissions = permissionRepository.findByLevelAndEntityId(Permission.LEVEL_USER,
                user.getId());
        if (userPermissions != null) {
            permissions.addAll(collectPermissionPatterns(userPermissions));
        }
        return removeDuplicates(permissions);
    }

    private List<String> getPermissionsForGroups(@NonNull User user) {
        final List<String> permissions = new ArrayList<>();

        // Adding permissions from group where user is member
        List<Group> groups = user.getGroups();
        if (groups != null && !groups.isEmpty()) {
            // Adding permissions specific for each group
            List<Permission> groupPermissions = permissionRepository.findByLevelAndEntityIdIn(Permission.LEVEL_GROUP,
                    groups.stream().map(g -> g.getId()).collect(Collectors.toList()));
            if (groupPermissions != null) {
                permissions.addAll(collectPermissionPatterns(groupPermissions));
            }

            // Add read permission for each user in groups
            Optional.ofNullable(userRepository.findUsersInGroups(groups)).ifPresent(users -> {
                users.forEach(u -> permissions.add(PermissionType.users().read().forPersistable(u).toString()));
            });

            List<User> usersInGroups = userRepository.findUsersInGroups(groups);
            if (usersInGroups != null && !usersInGroups.isEmpty()) {
                permissions.addAll(
                        usersInGroups.stream().map(u -> PermissionType.users().read().forPersistable(user).toString())
                                .collect(Collectors.toList()));
            }
        }
        return removeDuplicates(permissions);
    }

    @NonNull
    private List<String> collectPermissionPatterns(@NonNull List<Permission> permissions) {
        return permissions.stream().map(Permission::getEachPattern).flatMap(List::stream).collect(Collectors.toList());
    }

    @NonNull
    private List<String> removeDuplicates(@NonNull List<String> permissions) {
        return permissions.stream().distinct().sorted().collect(Collectors.toList());
    }
}
