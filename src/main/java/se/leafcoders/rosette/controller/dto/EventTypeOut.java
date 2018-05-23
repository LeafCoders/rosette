package se.leafcoders.rosette.controller.dto;

import java.util.List;

public class EventTypeOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private List<ResourceTypeRefOut> resourceTypes;
    private Boolean isPublic;


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

    public List<ResourceTypeRefOut> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceTypeRefOut> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

}
