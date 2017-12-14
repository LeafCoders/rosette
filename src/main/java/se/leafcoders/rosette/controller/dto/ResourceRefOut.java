package se.leafcoders.rosette.controller.dto;

import se.leafcoders.rosette.persistence.model.Resource;

public class ResourceRefOut {

    private Long id;
    private String name;
    private UserRefOut user;

    
    public ResourceRefOut(Resource resource) {
        this.id = resource.getId();
        this.name = resource.getName();
        this.user = new UserRefOut(resource.getUser());
    }
    
    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRefOut getUser() {
        return user;
    }

    public void setUser(UserRefOut user) {
        this.user = user;
    }
}
