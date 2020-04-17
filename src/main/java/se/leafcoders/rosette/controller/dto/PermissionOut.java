package se.leafcoders.rosette.controller.dto;

import lombok.Data;

@Data
public class PermissionOut {

    private Long id;
    private String name;
    private Integer level;
    private Long entityId;
    private String patterns;
}
