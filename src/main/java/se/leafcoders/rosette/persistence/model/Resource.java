package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.exception.ApiString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "resources")
public class Resource extends Persistable {

    private static final long serialVersionUID = 8585463249829759379L;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false, unique = true)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "resource_resourcetypes",
        joinColumns = { @JoinColumn(name = "resource_id") }, inverseJoinColumns = { @JoinColumn(name = "resourcetype_id") },
        uniqueConstraints = { @UniqueConstraint(columnNames = { "resource_id", "resourcetype_id" }) }
    )
    private List<ResourceType> resourceTypes = new ArrayList<>();

    @Column(name = "user_id", nullable = true, insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime lastUseTime;
    
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

    public void setUser(User user) {
        this.user = user;
        this.setUserId(user != null ? user.getId() : null);
    }
}
