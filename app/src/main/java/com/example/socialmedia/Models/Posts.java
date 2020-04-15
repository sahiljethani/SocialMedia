package com.example.socialmedia.Models;

public class Posts {

    private String userid;
    private String fullname;
    private String postImageUrl;
    private String postDescription;
    private String date;
    private String time;
    private String profileImage;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "userid='" + userid + '\'' +
                ", fullname='" + fullname + '\'' +
                ", postImageUrl='" + postImageUrl + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
