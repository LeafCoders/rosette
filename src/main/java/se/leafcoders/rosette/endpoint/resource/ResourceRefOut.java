package se.leafcoders.rosette.controller.dto;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.Resource;

@Data
public class ResourceRefOut {

    private Long id;
    private String name;
    private UserRefOut user;
    
    public ResourceRefOut(Resource resource) {
        this.id = resource.getId();
        this.name = resource.getName();
        this.user = resource.getUser() != null ? new UserRefOut(resource.getUser()) : null;
    }
}
