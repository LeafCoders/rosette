package se.leafcoders.rosette.model.event;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.reference.LocationRefOrText;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "events")
@ScriptAssert(lang = "javascript", script = "_this.endTime == null || (_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime))", message = "event.startBeforeEndTime")
public class Event extends IdBasedModel {

	@Indexed
	@HasRef(message = "event.eventType.notNull")
    private EventType eventType;

	@NotEmpty(message = "event.title.notEmpty")
	private String title;

	@Indexed
	@NotNull(message = "event.startTime.notNull")
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date startTime;

	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date endTime;

	private String description;

    private LocationRefOrText location;

    private Boolean showOnPalmate;

    @NotNull(message = "event.resources.notNull")
    private List<Resource> resources;
	
	@Override
	public void update(BaseModel updateFrom) {
		Event eventUpdate = (Event) updateFrom;
		if (eventUpdate.getTitle() != null) {
			setTitle(eventUpdate.getTitle());
		}
		if (eventUpdate.getStartTime() != null) {
			setStartTime(eventUpdate.getStartTime());
		}
		if (eventUpdate.getEndTime() != null) {
			setEndTime(eventUpdate.getEndTime());
		}
		if (eventUpdate.getDescription() != null) {
			setDescription(eventUpdate.getDescription());
		}
		if (eventUpdate.getLocation() != null) {
			setLocation(eventUpdate.getLocation());
		}
		if (eventUpdate.getShowOnPalmate() != null) {
			setShowOnPalmate(eventUpdate.getShowOnPalmate());
		}
		if (eventUpdate.getResources() != null) {
			setResources(eventUpdate.getResources());
		}
	}

	// Getters and setters

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocationRefOrText getLocation() {
        return location;
    }

    public void setLocation(LocationRefOrText location) {
        this.location = location;
    }

	public Boolean getShowOnPalmate() {
		return showOnPalmate;
	}

	public void setShowOnPalmate(Boolean showOnPalmate) {
		this.showOnPalmate = showOnPalmate;
	}

	public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
