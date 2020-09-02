package com.example.chatapp.model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String type;
    private String ads;
    private String key;
    private String date;
    private String title;
    private String time;

    public Chat(String sender, String receiver, String message, String type, String ads, String key, String date, String title, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.ads = ads;
        this.key = key;
        this.date = date;
        this.time = time;
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public Chat(){

    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getAds() {
        return ads;
    }

    public String getSender() {
        return sender;
    }


    public String getReceiver() {
        return receiver;
    }


    public String getMessage() {
        return message;
    }

}
