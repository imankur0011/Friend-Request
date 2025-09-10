package com.nagarro.ecommerce.model;

public class FriendRequestRequest {
    private String senderUsername;
    private String recipientUsername;

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public FriendRequestRequest(String senderUsername, String recipientUsername) {
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
    }

    
}
