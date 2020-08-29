package com.example.chatapp.model;

public class Time {
    private String author;
    private String day;
    private String description;
    private String distance;
    private String key;
    private String location;
    private Boolean setted_image;
    private String time;
    private String title;
    private String type;
    private Boolean verified;
    private Boolean pending;
    private Boolean achieved;


    public Time(){}

    public Time(String author, String day, String description, String distance, String key, String location, Boolean setted_image, String time, String title, String type, Boolean verified, Boolean pending, Boolean achieved) {
        this.author = author;
        this.day = day;
        this.description = description;
        this.distance = distance;
        this.key = key;
        this.location = location;
        this.setted_image = setted_image;
        this.time = time;
        this.title = title;
        this.type = type;
        this.verified = verified;
        this.pending = pending;
        this.achieved = achieved;
    }

    public Boolean getPending() {
        return pending;
    }

    public Boolean getAchieved() {
        return achieved;
    }

    public String getAuthor() {
        return author;
    }

    public String getDay() {
        return day;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }

    public String getKey() {
        return key;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getSetted_image() {
        return setted_image;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Boolean getVerified() {
        return verified;
    }
}



