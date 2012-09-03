package se.ryttargardskyrkan.rosette.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

	@Id
	private String id;
	@NotNull
	private String username;
	private String hashedPassword;
	private String status;
	private List<GroupMembership> groupMemberships;

	// Getters and setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public List<GroupMembership> getGroupMemberships() {
		return groupMemberships;
	}

	public void setGroupMemberships(List<GroupMembership> groupMemberships) {
		this.groupMemberships = groupMemberships;
	}
}
