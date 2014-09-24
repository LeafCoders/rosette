package se.ryttargardskyrkan.rosette.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends IdBasedModel {

	@Indexed(unique = true)
	@NotEmpty(message = "user.username.notEmpty")
	private String username;
	
	@Transient
	private String password;
	
	@JsonIgnore
	private String hashedPassword;
	private String status;

	@NotEmpty(message = "user.firstName.notEmpty")
	private String firstName;

	@NotEmpty(message = "user.lastName.notEmpty")
	private String lastName;

	// Getters and setters

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
