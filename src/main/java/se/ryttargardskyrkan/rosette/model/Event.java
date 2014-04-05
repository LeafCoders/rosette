package se.ryttargardskyrkan.rosette.model;

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

@Document(collection = "events")
@ScriptAssert(lang = "javascript", script = "_this.endTime == null || (_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime))", message = "event.startBeforeEndTime")
public class Event extends IdBasedModel {

	@NotEmpty(message = "event.title.notEmpty")
	private String title;

	@NotNull(message = "event.startTime.notNull")
	@Indexed
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date startTime;

	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date endTime;

	private String description;

    /* Id to the event type */
    private EventTypeReference eventType;

    private LocationReference location;

    /* List of ids to required user resource types */
    private List<String> requiredUserResourceTypes;

    private List<UserResource> userResources;

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

    public EventTypeReference getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeReference eventType) {
        this.eventType = eventType;
    }

    public LocationReference getLocation() {
        return location;
    }

    public void setLocation(LocationReference location) {
        this.location = location;
    }

    public List<String> getRequiredUserResourceTypes() {
        return requiredUserResourceTypes;
    }

    public void setRequiredUserResourceTypes(List<String> requiredUserResourceTypes) {
        this.requiredUserResourceTypes = requiredUserResourceTypes;
    }

    public List<UserResource> getUserResources() {
        return userResources;
    }

    public void setUserResources(List<UserResource> userResources) {
        this.userResources = userResources;
    }
}
