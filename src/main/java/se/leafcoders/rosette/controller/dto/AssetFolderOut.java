package se.leafcoders.rosette.controller.dto;

public class AssetFolderOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private String allowedMimeTypes;
    private Boolean staticFileKey;
    
    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public void setAllowedMimeTypes(String allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public Boolean getStaticFileKey() {
        return staticFileKey != null ? staticFileKey : false;
    }

    public void setStaticFileKey(Boolean staticFileKey) {
        this.staticFileKey = staticFileKey;
    }
    
}
