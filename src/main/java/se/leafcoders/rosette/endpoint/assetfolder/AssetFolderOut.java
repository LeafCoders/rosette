package se.leafcoders.rosette.controller.dto;

import lombok.Data;

@Data
public class AssetFolderOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private String allowedMimeTypes;
    private Boolean staticFileKey;
}
