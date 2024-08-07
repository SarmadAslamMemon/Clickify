package com.example.clickify;
public class User {
    private int id;
    private String user_email;
    private String isRegistered;


    public User(int id, String user_email, String isRegistered) {
        this.id = id;
        this.user_email = user_email;
        this.isRegistered = isRegistered;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }

    public String getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(String isRegistered) {
        this.isRegistered = isRegistered;
    }
}
