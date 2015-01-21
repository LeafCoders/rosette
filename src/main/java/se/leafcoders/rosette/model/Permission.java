package se.leafcoders.rosette.model;

import java.util.List;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permissions")
@ScriptAssert(lang = "javascript", script = "((_this.everyone?1:0)+(_this.user?1:0)+(_this.group?1:0)) == 1")
public class Permission extends IdBasedModel {
	
	@Indexed
	private Boolean everyone;

	@Indexed
	private User user;

	@Indexed
	private Group group;

	private List<String> patterns;

	@Override
	public void update(BaseModel updateFrom) {
		Permission permissionUpdate = (Permission) updateFrom;
    	if (permissionUpdate.getPatterns() != null) {
    		setPatterns(permissionUpdate.getPatterns());
    	}
	}
	
	// Getters and setters

	public Boolean getEveryone() {
		return everyone;
	}

	public void setEveryone(Boolean everyone) {
		this.everyone = everyone;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
}
