package com.example.socialmedia.Models;

public class User {
    private String username;
    private String email;
    private String profileImageUri;
    private String userid;

    public User(String username, String email, String profileImageUri, String userid) {
        this.username = username;
        this.email = email;
        this.profileImageUri = profileImageUri;
        this.userid = userid;
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profileImageUri='" + profileImageUri + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
