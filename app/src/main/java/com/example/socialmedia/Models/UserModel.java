package com.example.socialmedia.Models;

public class UserModel {
    private String username;
    private String email;
    private String profileImageUri;

    public UserModel(String username, String email, String profileImageUri) {
        this.username = username;
        this.email = email;
        this.profileImageUri = profileImageUri;
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

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profileImageUri='" + profileImageUri + '\'' +
                '}';
    }
}
