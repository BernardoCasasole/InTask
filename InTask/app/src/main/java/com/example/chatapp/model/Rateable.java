package com.example.chatapp.model;

public class Rateable {
    private String date;
    private String key;
    private String user;
    private String title;

    public Rateable(){}

    public Rateable(String date, String key, String user, String title) {
        this.date = date;
        this.key = key;
        this.user = user;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public String getUser() {
        return user;
    }
}
