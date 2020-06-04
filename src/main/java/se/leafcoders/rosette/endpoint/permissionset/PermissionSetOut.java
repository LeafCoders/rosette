package se.leafcoders.rosette.endpoint.permissionset;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
