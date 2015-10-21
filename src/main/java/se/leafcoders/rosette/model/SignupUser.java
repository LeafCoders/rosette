package se.leafcoders.rosette.model;

import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.leafcoders.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "signupUsers")
public class SignupUser extends IdBasedModel {

	@CreatedDate
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
    private Date createdTime;	

	@NotEmpty(message = "user.email.notEmpty")
	private String email;

	@Transient
	private String password;

	@JsonIgnore
	private String hashedPassword;

	@NotEmpty(message = "user.firstName.notEmpty")
	private String firstName;

	@NotEmpty(message = "user.lastName.notEmpty")
	private String lastName;

	@NotEmpty(message = "user.permissions.notEmpty")
	private String permissions;

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		SignupUser signupUserUpdate = (SignupUser) updateFrom;
		if (rawData.has("email")) {
			setEmail(signupUserUpdate.getEmail());
		}
		if (rawData.has("password") && !"".equals(signupUserUpdate.getPassword().trim())) {
 			String hashedPassword = new BCryptPasswordEncoder().encode(signupUserUpdate.getPassword());
			setHashedPassword(hashedPassword);
		}
		if (rawData.has("firstName")) {
			setFirstName(signupUserUpdate.getFirstName());
		}
		if (rawData.has("lastName")) {
			setLastName(signupUserUpdate.getLastName());
		}
		if (rawData.has("permissions")) {
			setPermissions(signupUserUpdate.getPermissions());
		}
	}

	// Getters and setters

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
}
