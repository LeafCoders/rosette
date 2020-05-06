package se.leafcoders.rosette.controller.dto;

import java.util.List;

import lombok.Data;

@Data
public class PermissionOut {

    private Long id;
    private String name;
    private Integer level;
    private Long entityId;
    private String patterns;
    private List<PermissionSetOut> permissionSets;
}
