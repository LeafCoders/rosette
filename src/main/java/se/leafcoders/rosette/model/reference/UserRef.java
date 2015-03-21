package se.leafcoders.rosette.model.reference;

import org.springframework.data.annotation.Transient;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.IdBasedModel;
import se.leafcoders.rosette.model.User;

public class UserRef extends IdBasedModel {
	private String firstName;
	private String lastName;

	public UserRef() {}
	
	public UserRef(User user) {
		id = user.getId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
	}
	
	@Override
	public void update(BaseModel updateFrom) {
		User userUpdate = (User) updateFrom;
		if (userUpdate.getFirstName() != null) {
			setFirstName(userUpdate.getFirstName());
		}
		if (userUpdate.getLastName() != null) {
			setLastName(userUpdate.getLastName());
		}
	}
	
	// Getters and setters
	
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
