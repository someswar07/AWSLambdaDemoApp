package org.personal.model;

import java.util.Map;

public class User {
    private int userId;
    private String userName;
    private String userEmail;
    private Map<String, Object> userAnalytics;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Map<String, Object> getUserAnalytics() {
        return userAnalytics;
    }

    public void setUserAnalytics(Map<String, Object> userAnalytics) {
        this.userAnalytics = userAnalytics;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userAnalytics=" + userAnalytics +
                '}';
    }
}
