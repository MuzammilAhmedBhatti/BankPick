package com.example.bankpick.models;

public class PaymentRequest {
    private String requestId;
    private String senderUid;
    private String receiverUid;
    private String senderName;
    private String receiverName;
    private double amount;
    private String description;
    private String status;
    private String timestamp;
    private long timestampMs;
    private boolean processing;

    public PaymentRequest() {
    }

    public PaymentRequest(String requestId, String senderUid, String receiverUid, String senderName,
            String receiverName, double amount, String description, String status,
            String timestamp, long timestampMs) {
        this.requestId = requestId;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.timestamp = timestamp;
        this.timestampMs = timestampMs;
        this.processing = false;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
