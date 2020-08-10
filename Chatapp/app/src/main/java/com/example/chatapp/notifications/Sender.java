package com.example.chatapp.notifications;

public class Sender {

    public Data data;
    public String to;
    public Notification notification;

    public Sender(Data data, String to, Notification notification) {
        this.data = data;
        this.to = to;
        this.notification = notification;
    }
}
