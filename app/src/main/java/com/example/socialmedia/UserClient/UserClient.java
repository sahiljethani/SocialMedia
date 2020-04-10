package com.example.socialmedia.UserClient;

import android.app.Application;

import com.example.socialmedia.Models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}