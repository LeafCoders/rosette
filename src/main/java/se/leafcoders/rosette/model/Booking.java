package se.leafcoders.rosette.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.leafcoders.rosette.model.reference.LocationRefOrText;
import se.leafcoders.rosette.validator.HasRefOrText;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

	@HasRefOrText(message = "booking.location.oneMustBeSet")
	private LocationRefOrText location;
	
	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		Booking bookingUpdate = (Booking) updateFrom;
		if (rawData.has("customerName")) {
			setCustomerName(bookingUpdate.getCustomerName());
		}
		if (rawData.has("startTime")) {
			setStartTime(bookingUpdate.getStartTime());
		}
		if (rawData.has("endTime")) {
			setEndTime(bookingUpdate.getEndTime());
		}
		if (rawData.has("location")) {
			setLocation(bookingUpdate.getLocation());
		}
	}

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

	public LocationRefOrText getLocation() {
		return location;
	}

	public void setLocation(LocationRefOrText location) {
		this.location = location;
	}
}
