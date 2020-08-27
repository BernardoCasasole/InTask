package com.example.chatapp.model;

public class Job {
    private String author;
    private String day;
    private String description;
    private String duration;
    private String key;
    private String location;
    private Float reward;
    private Boolean setted_image;
    private String time;
    private String title;
    private String type;
    private Boolean verified;

    public Job(){}


    public Job(String author, String day, String description, String duration, String key, String location, Float reward, Boolean setted_image, String time, String title, String type, Boolean verified) {
        this.author = author;
        this.day = day;
        this.description = description;
        this.duration = duration;
        this.key = key;
        this.location = location;
        this.reward = reward;
        this.setted_image = setted_image;
        this.time = time;
        this.title = title;
        this.type = type;
        this.verified = verified;
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

    public String getDuration() {
        return duration;
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

    public String getKey() {
        return key;
    }

    public Float getReward() {
        return reward;
    }
}
