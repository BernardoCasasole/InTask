package com.example.chatapp.notifications;

public class Data {

    private String user;
    private String sent;
    private String body;
    private String title;

    public Data(String user, String sent, String title, String body) {
        this.user = user;
        this.sent = sent;
        this.body = body;
        this.title = title;


    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
