package se.leafcoders.rosette.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

@Document(collection = "users")
public class User extends IdBasedModel {

	@Indexed(unique = true)
	@NotEmpty(message = "user.email.notEmpty")
	@Email(message = "user.email.invalid")
	private String email;
	
	@Transient
	private String password;
	
	@JsonIgnore
	private String hashedPassword;

	@NotEmpty(message = "user.firstName.notEmpty")
	private String firstName;

	@NotEmpty(message = "user.lastName.notEmpty")
	private String lastName;

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		User userUpdate = (User) updateFrom;
		if (rawData.has("email")) {
			setEmail(userUpdate.getEmail());
		}
		if (rawData.has("password") && !"".equals(userUpdate.getPassword().trim())) {
			String hashedPassword = new BCryptPasswordEncoder().encode(userUpdate.getPassword());
			setHashedPassword(hashedPassword);
		}
		if (rawData.has("firstName")) {
			setFirstName(userUpdate.getFirstName());
		}
		if (rawData.has("lastName")) {
			setLastName(userUpdate.getLastName());
		}
	}

	// Getters and setters

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

	// Helpers
	
	@Transient
	public String getFullName() {
		String name = "";
		
		String delimiter = "";
		
		if (this.getFirstName() != null) {
			name =  this.getFirstName();
			delimiter = " ";
		}
		
		if (this.getLastName() != null) {
			name += delimiter + this.getLastName();
		}
		
		return name;
	}

    @Transient
    public void setFullName(String fullName) {
        // nothing
    }
}
