package com.example.chatapp.model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String type;
    private String ads;
    private String key;

    public Chat(String sender, String receiver, String message, String type, String ads, String key) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.ads = ads;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Chat(){

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
