package se.leafcoders.rosette.endpoint.permission;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.permission.PermissionType;
import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSet;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetOut;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetService;

@Service
public class PermissionService extends PersistenceService<Permission, PermissionIn, PermissionOut> {

    @Autowired
    private PermissionSetService permissionSetService;

    public PermissionService(PermissionRepository repository) {
        super(Permission.class, PermissionType::permissions, repository);
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
        dto.setPermissionSets(item.getPermissionSets().stream()
                .map(permissionSet -> new PermissionSetOut(permissionSet)).collect(Collectors.toList()));
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
    public void delete(Long id, boolean checkPermissions) {
        super.delete(id, checkPermissions);
        securityService.resetPermissionCache();
    }

    public List<PermissionSet> getPermissionSets(Long permissionId) {
        return read(permissionId, true).getPermissionSets();
    }

    public List<PermissionSet> addPermissionSet(Long permissionId, Long permissionSetId) {
        checkPermission(PermissionType.permissions().update().forId(permissionId));
        if (repo().isPermissionSetInPermission(permissionSetId, permissionId)) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
        Permission permission = read(permissionId, true);
        PermissionSet permissionSet = permissionSetService.read(permissionSetId, true);
        permission.addPermissionSet(permissionSet);
        return repository.save(permission).getPermissionSets();
    }

    public List<PermissionSet> removePermissionSet(Long permissionId, Long permissionSetId) {
        checkPermission(PermissionType.permissions().update().forId(permissionId));
        Permission permission = read(permissionId, true);
        PermissionSet permissionSet = permissionSetService.read(permissionSetId, true);
        permission.removePermissionSet(permissionSet);
        return repository.save(permission).getPermissionSets();
    }

}
