package se.ryttargardskyrkan.rosette.model.event;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.model.EventType;
import se.ryttargardskyrkan.rosette.model.IdBasedModel;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.validator.HasIdRef;

@ScriptAssert(lang = "javascript", script = "_this.endTime == null || (_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime))", message = "event.startBeforeEndTime")
public class EventCreateRequest extends IdBasedModel {

	@NotEmpty(message = "event.title.notEmpty")
	private String title;

	@NotNull(message = "event.startTime.notNull")
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date startTime;

	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date endTime;

	@HasIdRef(message = "event.eventType.notNull")
    private ObjectReference<EventType> eventType;

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

    public ObjectReference<EventType> getEventType() {
        return eventType;
    }

    public void setEventType(ObjectReference<EventType> eventType) {
        this.eventType = eventType;
    }
}
