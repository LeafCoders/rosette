package se.leafcoders.rosette.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.security.RosettePasswordService;

@Document(collection = "users")
public class User extends IdBasedModel {

	@Indexed(unique = true)
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
	@Email(message = "user.email.invalid")
	private String email;

	@Override
	public void update(BaseModel updateFrom) {
		User userUpdate = (User) updateFrom;
		if (userUpdate.getUsername() != null) {
			setUsername(userUpdate.getUsername());
		}
		if (userUpdate.getPassword() != null && !"".equals(userUpdate.getPassword().trim())) {
			String hashedPassword = new RosettePasswordService().encryptPassword(userUpdate.getPassword());
			setHashedPassword(hashedPassword);
		}
		if (userUpdate.getFirstName() != null) {
			setFirstName(userUpdate.getFirstName());
		}
		if (userUpdate.getLastName() != null) {
			setLastName(userUpdate.getLastName());
		}
		if (userUpdate.getEmail() != null) {
			setEmail(userUpdate.getEmail());
		}
	}

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
