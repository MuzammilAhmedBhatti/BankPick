package com.example.bankpick.models;

public class Transaction {
    private String transactionId;
    private String name;
    private String category;
    private double amount;
    private String icon;
    private String date;
    private String time;

    public Transaction() {
    }

    public Transaction(String transactionId, String name, String category, double amount, String icon, String date, String time) {
        this.transactionId = transactionId;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.icon = icon;
        this.date = date;
        this.time = time;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
