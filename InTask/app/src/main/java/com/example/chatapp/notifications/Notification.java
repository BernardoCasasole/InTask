package com.example.chatapp.notifications;

public class Notification {

    public String body;
    public String title;
    public String click_action;

    public Notification(String body, String title, String click_action) {
        this.body = body;
        this.title = title;
        this.click_action = click_action;
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

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }
}
