package se.leafcoders.rosette.persistence.service;

import static se.leafcoders.rosette.permission.PermissionAction.READ;
import static se.leafcoders.rosette.permission.PermissionAction.UPDATE;
import static se.leafcoders.rosette.permission.PermissionType.USERS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import se.leafcoders.rosette.controller.dto.PermissionIn;
import se.leafcoders.rosette.controller.dto.PermissionOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.model.Permission;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.PermissionRepository;
import se.leafcoders.rosette.persistence.repository.UserRepository;

@Service
public class PermissionService extends PersistenceService<Permission, PermissionIn, PermissionOut> {

    @Autowired
    private UserRepository userRepository;

    public PermissionService(PermissionRepository repository) {
        super(Permission.class, PermissionType.PERMISSIONS, repository);
    }

    @Override
    protected Permission convertFromInDTO(PermissionIn dto, JsonNode rawIn, Permission item) {
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("level")) {
            item.setLevel(dto.getLevel());
        }
        if (rawIn == null || rawIn.has("entityId")) {
            item.setEntityId(dto.getEntityId());
        }
        if (rawIn == null || rawIn.has("patterns")) {
            item.setPatterns(dto.getPatterns());
        }
        return item;
    }

    @Override
    protected PermissionOut convertToOutDTO(Permission item) {
        PermissionOut dto = new PermissionOut();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setLevel(item.getLevel());
        dto.setEntityId(item.getEntityId());
        dto.setPatterns(item.getPatterns());
        return dto;
    }

    protected PermissionRepository repo() {
        return (PermissionRepository) repository;
    }

    @Override
    public Permission create(PermissionIn data, boolean checkPermissions) {
        securityService.resetPermissionCache();
        return super.create(data, checkPermissions);
    }

    @Override
    public ResponseEntity<Void> delete(Long id, boolean checkPermissions) {
        ResponseEntity<Void> re = super.delete(id, checkPermissions);
        securityService.resetPermissionCache();
        return re;
    }

    public List<String> getForUser(Long userId) {
        List<String> permissionStrings = new LinkedList<String>();

        // Adding permissions for everyone (implicit also from Public)
        permissionStrings.addAll(getForEveryone());

        // Adding permissions for specified user
        User user = userId != null ? userRepository.findOne(userId) : null;
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

        List<Permission> permissions = repo().findByLevel(Permission.LEVEL_ALL_USERS);
        if (permissions != null) {
            permissionStrings.addAll(permissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream).collect(Collectors.toList()));
        }
        return permissionStrings;
    }

    public List<String> getForPublic() {
        List<Permission> permissions = repo().findByLevel(Permission.LEVEL_PUBLIC);
        if (permissions != null) {
            return permissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream).collect(Collectors.toList());
        }
        return new LinkedList<String>();
    }

    private List<String> getPermissionsForUser(User user) {
        List<String> permissions = new ArrayList<String>();

        // Add read/update permissions for own user
        permissions.add(new PermissionValue(USERS, READ, user.getId().toString()).toString());
        permissions.add(new PermissionValue(USERS, UPDATE, user.getId().toString()).toString());

        // Adding permissions specific for this user
        List<Permission> userPermissions = repo().findByLevelAndEntityId(Permission.LEVEL_USER, user.getId());
        if (userPermissions != null) {
            permissions.addAll(userPermissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream).collect(Collectors.toList()));
        }
        return permissions;
    }

    private List<String> getPermissionsForGroups(User user) {
        List<String> permissions = new ArrayList<String>();

        // Adding permissions from group where user is member
        List<Group> groups = user.getGroups();
        if (groups != null && !groups.isEmpty()) {
            // Adding permissions specific for each group
            List<Permission> groupPermissions = repo().findByLevelAndEntityIdIn(
                Permission.LEVEL_GROUP, groups.stream().map(g -> g.getId()).collect(Collectors.toList())
            );
            if (groupPermissions != null) {
                permissions.addAll(groupPermissions.stream().map(p -> p.getEachPattern()).flatMap(List::stream).collect(Collectors.toList()));
            }

            // Add read permission for each user in groups
            List<User> usersInGroups = userRepository.findUsersInGroups(groups);
            if (usersInGroups != null && !usersInGroups.isEmpty()) {
                permissions
                    .addAll(usersInGroups.stream().map(u -> new PermissionValue(USERS, READ, u.getId().toString()).toString()).collect(Collectors.toList()));
            }
        }
        return permissions;
    }

}
