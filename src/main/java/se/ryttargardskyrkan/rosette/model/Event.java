package se.ryttargardskyrkan.rosette.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
public class Event {

	@Id
	private String id;
	
	@NotNull
	private String title;
	
	private Date startTime;
	private Date endTime;
	
	private EntityReference conductorOfMeeting;
	
	private List<EntityReference> soundTechnicians;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public EntityReference getConductorOfMeeting() {
		return conductorOfMeeting;
	}

	public void setConductorOfMeeting(EntityReference conductorOfMeeting) {
		this.conductorOfMeeting = conductorOfMeeting;
	}

	public List<EntityReference> getSoundTechnicians() {
		return soundTechnicians;
	}

	public void setSoundTechnicians(List<EntityReference> soundTechnicians) {
		this.soundTechnicians = soundTechnicians;
	}

	
	
}
