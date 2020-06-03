package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import se.leafcoders.rosette.persistence.model.PermissionSet;

@NoArgsConstructor
@Data
public class PermissionSetOut {

    private Long id;
    private String name;
    private String patterns;

    public PermissionSetOut(@NonNull PermissionSet permissionSet) {
        this.id = permissionSet.getId();
        this.name = permissionSet.getName();
        this.patterns = permissionSet.getPatterns();
    }
}
