package se.ryttargardskyrkan.rosette.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permissions")
public class Permission {
	@Id
	private String id;
	
	@Indexed
	private boolean anyone;
	
	@Indexed
	private String userId;
	
	@Indexed
	private String groupId;
	
	private List<String> patterns;
	
	// Getters and setters

	public String getId() {
		return id;
	}

	public boolean isAnyone() {
		return anyone;
	}

	public void setAnyone(boolean anyone) {
		this.anyone = anyone;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
}
