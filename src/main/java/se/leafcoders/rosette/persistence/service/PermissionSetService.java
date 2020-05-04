package se.leafcoders.rosette.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;

import se.leafcoders.rosette.controller.dto.PermissionSetIn;
import se.leafcoders.rosette.controller.dto.PermissionSetOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.PermissionSet;
import se.leafcoders.rosette.persistence.repository.PermissionSetRepository;

@Service
public class PermissionSetService extends PersistenceService<PermissionSet, PermissionSetIn, PermissionSetOut> {

    public PermissionSetService(PermissionSetRepository repository) {
        super(PermissionSet.class, PermissionType::permissionSets, repository);
    }

    @Override
    protected PermissionSet convertFromInDTO(PermissionSetIn dto, JsonNode rawIn, PermissionSet item) {
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("patterns")) {
            item.setPatterns(dto.getPatterns());
        }
        return item;
    }

    @Override
    protected PermissionSetOut convertToOutDTO(PermissionSet item) {
        PermissionSetOut dto = new PermissionSetOut();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setPatterns(item.getPatterns());
        return dto;
    }
}
