package se.leafcoders.rosette.controller.dto;

import lombok.Data;

@Data
public class ResourceTypeOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private Long displayOrder;
}
