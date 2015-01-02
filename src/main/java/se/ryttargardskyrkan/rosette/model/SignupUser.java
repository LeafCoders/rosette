package se.ryttargardskyrkan.rosette.model;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonDeserializer;
import se.ryttargardskyrkan.rosette.converter.RosetteDateTimeTimezoneJsonSerializer;

@Document(collection = "signupUsers")
public class SignupUser extends IdBasedModel {

	@CreatedDate
	@JsonSerialize(using = RosetteDateTimeTimezoneJsonSerializer.class)
	@JsonDeserialize(using = RosetteDateTimeTimezoneJsonDeserializer.class)
    private Date createdTime;	

	@NotEmpty(message = "user.username.notEmpty")
	private String username;
	
	@Transient
	private String password;

	@JsonIgnore
	private String hashedPassword;

	@NotEmpty(message = "user.firstName.notEmpty")
	private String firstName;

	@NotEmpty(message = "user.lastName.notEmpty")
	private String lastName;

	@NotEmpty(message = "user.email.notEmpty")
	private String email;

	@NotEmpty(message = "user.permissions.notEmpty")
	private String permissions;

	// Getters and setters

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
}
