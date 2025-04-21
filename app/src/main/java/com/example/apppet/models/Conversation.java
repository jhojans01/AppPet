package com.example.apppet.models;

public class Conversation {
    private int ownerId;
    private String ownerName;
    private String lastMessage;
    private String timestamp;

    public Conversation(int ownerId, String ownerName, String lastMessage, String timestamp) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // Getters (y constructor vacío si deseas)
    public int getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getLastMessage() { return lastMessage; }
    public String getTimestamp() { return timestamp; }
}
