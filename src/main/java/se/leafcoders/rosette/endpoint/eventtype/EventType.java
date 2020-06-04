package se.leafcoders.rosette.endpoint.eventtype;

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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.IdAlias;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "eventtypes")
public class EventType extends Persistable {

    private static final long serialVersionUID = -854979754687811329L;

    @IdAlias
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
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

    @NotNull(message = ApiString.NOT_NULL)
    private Boolean isPublic;

    // Getters and setters

    public List<ResourceType> getResourceTypes() {
        if (resourceTypes == null) {
            resourceTypes = new ArrayList<>();
        }
        return resourceTypes;
    }

    public void addResourceType(ResourceType resourceType) {
        getResourceTypes().add(resourceType);
    }

    public void removeResourceType(ResourceType resourceType) {
        getResourceTypes().remove(resourceType);
    }
}
