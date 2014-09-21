package se.ryttargardskyrkan.rosette.model;

import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permissions")
public class Permission extends IdBasedModel {
	
	@Indexed
	private Boolean everyone;
	
	@Indexed
	private ObjectReference<User> user;

	@Indexed
	private ObjectReference<Group> group;

	private List<String> patterns;
	
	// Getters and setters

	public Boolean getEveryone() {
		return everyone;
	}

	public void setEveryone(Boolean everyone) {
		this.everyone = everyone;
	}

	public ObjectReference<User> getUser() {
		return user;
	}

	public void setUser(ObjectReference<User> user) {
		this.user = user;
	}

	public ObjectReference<Group> getGroup() {
		return group;
	}

	public void setGroup(ObjectReference<Group> group) {
		this.group = group;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
}
