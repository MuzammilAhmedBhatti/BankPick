package com.example.bankpick.models;

public class Loan {
    private String loanId;
    private String userId;
    private String cardId;
    private double amount;
    private String reason;
    private String status; // pending, approved, rejected
    private String timestamp;
    private String userName;
    private String cardNumber;

    public Loan() {}

    public Loan(String loanId, String userId, String cardId, double amount,
                String reason, String status, String timestamp,
                String userName, String cardNumber) {
        this.loanId = loanId;
        this.userId = userId;
        this.cardId = cardId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
        this.userName = userName;
        this.cardNumber = cardNumber;
    }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
}
