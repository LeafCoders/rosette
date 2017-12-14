package se.leafcoders.rosette.persistence.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "eventtypes")
public class EventType extends Persistable {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Column(nullable = false, unique = true)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "eventtype_resourcetypes",
        joinColumns = { @JoinColumn(name = "eventtype_id") },
        inverseJoinColumns = { @JoinColumn(name = "resourcetype_id") },
        uniqueConstraints = { @UniqueConstraint(columnNames = { "eventtype_id", "resourcetype_id" }) }
    )
    private List<ResourceType> resourceTypes = new ArrayList<>();


    public EventType() {
    }

    // Getters and setters

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

    public List<ResourceType> getResourceTypes() {
        if (resourceTypes == null) {
            resourceTypes = new ArrayList<>();
        }
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public void addResourceType(ResourceType resourceType) {
        getResourceTypes().add(resourceType);
    }

    public void removeResourceType(ResourceType resourceType) {
        getResourceTypes().remove(resourceType);
    }

}
