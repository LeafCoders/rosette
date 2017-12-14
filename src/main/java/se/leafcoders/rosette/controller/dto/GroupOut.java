package se.leafcoders.rosette.controller.dto;

import java.util.List;

public class GroupOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private List<UserRefOut> users;

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

    public List<UserRefOut> getUsers() {
        return users;
    }

    public void setUsers(List<UserRefOut> users) {
        this.users = users;
    }
}
