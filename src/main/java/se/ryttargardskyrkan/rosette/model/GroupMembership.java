package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Document(collection = "groupMemberships")
public class GroupMembership extends IdBasedModel {

	@NotNull(message = "groupMembership.groupId.notNull")
    @Indexed
    private String groupId;
    @NotNull(message = "groupMembership.userId.notNull")
    @Indexed
    private String userId;
    @Transient
    private String username;
    @Transient
    private String userFullName;
    @Transient
    private String groupName;

    // Getters and setters

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
