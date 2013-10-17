package se.ryttargardskyrkan.rosette.model;

/**
 * Created with IntelliJ IDEA.
 * User: larsa
 * Date: 2013-10-07
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class UserReference {
    private String userId;

    private String userFullName;

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
