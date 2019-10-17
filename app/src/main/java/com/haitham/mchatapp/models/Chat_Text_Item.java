package com.haitham.mchatapp.models;

public class Chat_Text_Item {

    public String text;
    String time;
    String username;

    public Chat_Text_Item() {

        //Empty constractor is required.
    }

    public Chat_Text_Item(String text, String time, String username) {
        this.text = text;
        this.time = time;
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
