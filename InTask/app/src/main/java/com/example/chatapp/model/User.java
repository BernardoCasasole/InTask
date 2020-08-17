package com.example.chatapp.model;

public class User {

    private String username;
    private String id;
    private String setted_image;
    private String mail;
    private String ratings;
    private String average_ratings;

    public User(String username, String id, String setted_image, String mail, String ratings, String average_ratings) {
        this.username = username;
        this.id = id;
        this.setted_image = setted_image;
        this.mail = mail;
        this.ratings = ratings;
        this.average_ratings = average_ratings;
    }

    public User(){


    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSetted_image() {
        return Boolean.parseBoolean(setted_image);
    }

    public void setSetted_image(String setted_image) {
        this.setted_image = setted_image;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getAverage_ratings() {
        return average_ratings;
    }

    public void setAverage_ratings(String average_ratings) {
        this.average_ratings = average_ratings;
    }
}
