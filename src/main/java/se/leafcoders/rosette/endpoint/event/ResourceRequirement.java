package se.leafcoders.rosette.endpoint.event;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.endpoint.resource.Resource;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "resourcerequirements", uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "resourcetype_id" }))
public class ResourceRequirement extends Persistable {

    private static final long serialVersionUID = 699124896259923524L;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    
    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "resourcetype_id")
    private ResourceType resourceType;

    // Must be a Set. Otherwise the two level FETCH will not work in EventsController
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "resourcerequirement_resources",
        joinColumns = @JoinColumn(name = "resourcerequirement_id"),
        inverseJoinColumns = @JoinColumn(name = "resource_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = { "resourcerequirement_id", "resource_id" })
    )
    private Set<Resource> resources;

    public ResourceRequirement(Event event, ResourceType resourceType) {
        this.event = event;
        this.resourceType = resourceType;
    }

    // Getters and setters

    public Set<Resource> getResources() {
        if (resources == null) {
            resources = new HashSet<>();
        }
        return resources;
    }

    public void addResource(Resource resource) {
        getResources().add(resource);
    }
    
    public void removeResource(Resource resource) {
        getResources().remove(resource);
    }

}
