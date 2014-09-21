package se.ryttargardskyrkan.rosette.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.ryttargardskyrkan.rosette.validator.HasIdRefOrText;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "bookings")
@ScriptAssert(lang = "javascript", script = "_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime)", message = "booking.startBeforeEndTime")
public class Booking extends IdBasedModel {

	@NotEmpty(message = "booking.customerName.notEmpty")
	private String customerName;

	// Start time of booking
	@NotNull(message = "booking.startTime.notNull")
	@Indexed
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date startTime;

	// End time of booking
	@NotNull(message = "booking.endTime.notNull")
	@Indexed
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
	private Date endTime;

	@HasIdRefOrText(message = "booking.location.mustBeSet")
	private ObjectReferenceOrText<Location> location;

	// Getters and setters

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public ObjectReferenceOrText<Location> getLocation() {
		return location;
	}

	public void setLocation(ObjectReferenceOrText<Location> location) {
		this.location = location;
	}
}
