package se.leafcoders.rosette.endpoint.permissionset;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.permission.PermissionType;
import se.leafcoders.rosette.core.persistable.PersistenceService;

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
