package se.leafcoders.rosette.model.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.reference.LocationRefOrText;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.model.resource.UserResource;
import se.leafcoders.rosette.validator.CheckReference;
import se.leafcoders.rosette.validator.CheckReferenceArray;
import se.leafcoders.rosette.validator.HasRef;

@Document(collection = "events")
@ScriptAssert(lang = "javascript", script = "_this.endTime == null || (_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime))", message = "event.startBeforeEndTime")
public class Event extends IdBasedModel {

	@HasRef(message = "event.eventType.notNull")
    @CheckReference
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

    @Length(max = 200, message = "error.description.max200Chars")
	private String description;

    @CheckReference(model = Location.class, dbKey = "location.ref.id")
    private LocationRefOrText location;

    private Boolean isPublic;

    @Valid
    @CheckReferenceArray(model = Resource.class)
    private List<Resource> resources;

    private Integer version = 1;

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		Event eventUpdate = (Event) updateFrom;
		if (eventUpdate.getEventType() != null && !eventUpdate.getEventType().getId().equals(getEventType().getId())) {
			throw new SimpleValidationException(new ValidationError("event", "event.eventType.notAllowedToChange"));
		}

		if (rawData.has("title")) {
			setTitle(eventUpdate.getTitle());
		}
		if (rawData.has("startTime")) {
			setStartTime(eventUpdate.getStartTime());
		}
		if (rawData.has("endTime")) {
			setEndTime(eventUpdate.getEndTime());
		}
		if (rawData.has("description")) {
			setDescription(eventUpdate.getDescription());
		}
		if (rawData.has("location")) {
			setLocation(eventUpdate.getLocation());
		}
		if (rawData.has("isPublic") &&
				eventUpdate.getIsPublic() != null &&
				eventType.getHasPublicEvents().getAllowChange()) {
			setIsPublic(eventUpdate.getIsPublic());
		}
		if (rawData.has("resources")) {
			setResources(eventUpdate.getResources());
		}
	}

	public String expandedDescription() {
        if (description == null || description.isEmpty()) {
            return description;
        }
        
        HashMap<String,String> replacements = new HashMap<String,String>();
        resources.forEach((Resource resource) -> {
            if (resource.getType().equals("user")) {
                UserResource userResource = (UserResource) resource;
                replacements.put(userResource.getResourceType().getId(), userResource.getUsers().namesString());
            }
        });
        
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(description);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            builder.append(description.substring(i, matcher.start()));
            String[] parts = matcher.group().substring(1, matcher.group().length() - 1).split("#");
            if (parts.length >= 2) {
                String replacement = replacements.get(parts[1]);
                if (replacement != null && !replacement.isEmpty()) {
                    builder.append(parts[0] + replacement);
                }
            }
            i = matcher.end();
        }
        builder.append(description.substring(i, description.length()));
        return builder.toString().replaceAll("\n+", "\n");
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

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
