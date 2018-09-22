package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

public class ResourceOut {

    private Long id;
    private String name;
    private String description;
    private List<ResourceTypeRefOut> resourceTypes;
    private UserRefOut user;
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastUseTime;

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

    public UserRefOut getUser() {
        return user;
    }

    public void setUser(UserRefOut user) {
        this.user = user;
    }

    public LocalDateTime getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(LocalDateTime lastUseTime) {
        this.lastUseTime = lastUseTime;
    }
}
