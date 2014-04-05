package se.ryttargardskyrkan.rosette.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Min;

@Document(collection = "userResourceTypes")
public class UserResourceType extends IdBasedModel {

	@NotEmpty(message = "userResourceType.name.notEmpty")
    private String name;
    private String groupId;

    /* The order of presentation for resources in events */
    @Min(0)
    private int sortOrder;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
