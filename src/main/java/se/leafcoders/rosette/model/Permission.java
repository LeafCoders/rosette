package se.leafcoders.rosette.model;

import java.util.Arrays;
import java.util.List;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import se.leafcoders.rosette.model.reference.UserRef;
import se.leafcoders.rosette.validator.ValidPermissions;
import com.fasterxml.jackson.databind.JsonNode;

@Document(collection = "permissions")
@ScriptAssert(lang = "javascript", script = "((_this.everyone?1:0)+(_this.user?1:0)+(_this.group?1:0)) == 1")
public class Permission extends IdBasedModel {
	
	@Indexed
	private Boolean everyone;

	@Indexed
	private UserRef user;

	@Indexed
	private Group group;

	@ValidPermissions
	private List<String> patterns;

	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		Permission permissionUpdate = (Permission) updateFrom;
    	if (rawData.has("patterns")) {
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

	public UserRef getUser() {
		return user;
	}

	public void setUser(UserRef user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<String> getPatterns() {
		return cleanPatterns(patterns);
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = cleanPatterns(patterns);
	}
	
	private List<String> cleanPatterns(List<String> patternsToClean) {
		if (patternsToClean != null) {
			patternsToClean.removeAll(Arrays.asList("", null));
			
			for (int index = 0; index < patternsToClean.size(); ++index) {
				String permission = patternsToClean.get(index);
				while (permission.endsWith(":*")) {
					permission = permission.substring(0, permission.length() - 2);
				}
				patternsToClean.set(index, permission);
			}
		}
		return patternsToClean;
	}
}
