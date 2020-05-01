package se.leafcoders.rosette.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.controller.dto.PermissionIn;
import se.leafcoders.rosette.controller.dto.PermissionOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Permission;
import se.leafcoders.rosette.persistence.repository.PermissionRepository;

@Service
public class PermissionService extends PersistenceService<Permission, PermissionIn, PermissionOut> {

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
}
