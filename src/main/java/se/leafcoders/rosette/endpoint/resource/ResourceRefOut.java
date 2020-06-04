package se.leafcoders.rosette.endpoint.resource;

import lombok.Data;
import se.leafcoders.rosette.endpoint.user.UserRefOut;

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
