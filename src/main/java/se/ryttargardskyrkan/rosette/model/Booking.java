package se.ryttargardskyrkan.rosette.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import se.ryttargardskyrkan.rosette.validator.HasIdOrText;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "bookings")
@ScriptAssert(lang = "javascript", script = "_this.endTime != null && _this.startTime !=null && _this.startTime.before(_this.endTime)", message = "booking.startBeforeEndTime")
public class Booking {
    @Id
    private String id;

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

    @NotNull(message = "booking.location.notNull")
    @HasIdOrText
    private IdOrText location;

    // LocationData will not be stored in database. It will only be set for GET requests
    @Transient
    private Location locationData;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public IdOrText getLocation() {
        return location;
    }

    public void setLocation(IdOrText location) {
        this.location = location;
    }

    public Location getLocationData() {
        return locationData;
    }

    public void setLocationData(Location locationData) {
        this.locationData = locationData;
    }
}
