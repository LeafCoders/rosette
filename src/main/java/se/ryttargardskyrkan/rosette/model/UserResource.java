package se.ryttargardskyrkan.rosette.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: larsa
 * Date: 2013-10-07
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class UserResource {
    private String userResourceTypeId;

    private String userResourceTypeName;

    private List<UserReference> userReferences;

    // Getters and setters

    public String getUserResourceTypeId() {
        return userResourceTypeId;
    }

    public void setUserResourceTypeId(String userResourceTypeId) {
        this.userResourceTypeId = userResourceTypeId;
    }

    public String getUserResourceTypeName() {
        return userResourceTypeName;
    }

    public void setUserResourceTypeName(String userResourceTypeName) {
        this.userResourceTypeName = userResourceTypeName;
    }

    public List<UserReference> getUserReferences() {
        return userReferences;
    }

    public void setUserReferences(List<UserReference> userReferences) {
        this.userReferences = userReferences;
    }
}