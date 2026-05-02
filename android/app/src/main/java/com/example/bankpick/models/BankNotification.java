package com.example.bankpick.models;

public class BankNotification {
    private String id;
    private String title;
    private String message;
    private String time;
    private boolean unread;
    private String icon;
    private String color;

    public BankNotification() {}

    public BankNotification(String id, String title, String message, String time, boolean unread, String icon, String color) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.unread = unread;
        this.icon = icon;
        this.color = color;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isUnread() { return unread; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
}
