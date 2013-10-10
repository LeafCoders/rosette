package se.ryttargardskyrkan.rosette.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: larsa
 * Date: 2013-10-07
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
@Document(collection = "userResourceTypes")
public class UserResourceType {
    @Id
    private String id;
    @NotEmpty(message = "userResourceType.name.notEmpty")
    private String name;
    private String groupId;
    @Min(0)
    private int sortOrder;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
