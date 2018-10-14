package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Version;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.persistence.validator.DateTimeAfter;

@Entity
@Table(name = "events_table")
@DateTimeAfter(startDateTime = "startTime", endDateTime = "endTime", errorAt = "endTime")
public class Event extends Persistable {

    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime startTime;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime endTime;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    @Column(name = "eventtype_id", nullable = false, insertable = false, updatable = false)
    private Long eventTypeId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "eventtype_id")
    private EventType eventType;

    // Must be a Set. Otherwise the two level FETCH will not work in EventsController
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ResourceRequirement> resourceRequirements;

    @NotNull(message = ApiString.NOT_NULL)
    private Boolean isPublic;

    @Version
    private Integer version;


    public Event() {
    }

    // Getters and setters

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        this.setEventTypeId(eventType != null ? eventType.getId() : null);
    }

    public Set<ResourceRequirement> getResourceRequirements() {
        if (resourceRequirements == null) {
            resourceRequirements = new HashSet<>();
        }
        return resourceRequirements;
    }

    public void setResourceRequirements(Set<ResourceRequirement> resourceRequirement) {
        this.resourceRequirements = resourceRequirement;
    }

    public void addResourceRequirement(ResourceRequirement resourceRequirement) {
        getResourceRequirements().add(resourceRequirement);
    }

    public void removeResourceRequirement(ResourceRequirement resourceRequirement) {
        getResourceRequirements().remove(resourceRequirement);
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getVersion() {
        return version;
    }

}
