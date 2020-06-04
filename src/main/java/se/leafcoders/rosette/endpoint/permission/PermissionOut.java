package se.leafcoders.rosette.endpoint.permission;

import java.util.List;

import lombok.Data;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetOut;

@Data
public class PermissionOut {

    private Long id;
    private String name;
    private Integer level;
    private Long entityId;
    private String patterns;
    private List<PermissionSetOut> permissionSets;
}
