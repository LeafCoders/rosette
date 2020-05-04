package se.leafcoders.rosette.controller.dto;

import lombok.Data;

@Data
public class PermissionSetOut {

    private Long id;
    private String name;
    private String patterns;
}
