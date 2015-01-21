package se.ryttargardskyrkan.rosette.model.event;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.ryttargardskyrkan.rosette.model.BaseModel;
import se.ryttargardskyrkan.rosette.model.EventType;
import se.ryttargardskyrkan.rosette.model.IdBasedModel;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.model.ObjectReferenceOrText;
import se.ryttargardskyrkan.rosette.model.resource.Resource;
import se.ryttargardskyrkan.rosette.validator.HasRef;

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

    private ObjectReferenceOrText<Location> location;

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

    public ObjectReferenceOrText<Location> getLocation() {
        return location;
    }

    public void setLocation(ObjectReferenceOrText<Location> location) {
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
