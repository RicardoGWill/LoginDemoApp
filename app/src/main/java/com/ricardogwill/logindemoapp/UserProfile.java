package com.ricardogwill.logindemoapp;

public class UserProfile {
    public String userName;
    public String userEmail;
    public String userAge;

    // Use a constructor without parameters if you use getters and/or setters.
    public UserProfile() {

    }
    // This is another constructor WITH parameters.
    public UserProfile(String userName, String userEmail, String userAge) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAge = userAge;
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

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }
}

