package com.example.apppet.models;

import com.google.gson.annotations.SerializedName;

public class Conversation {
    @SerializedName("ownerId")
    private int ownerId;

    @SerializedName("ownerName")
    private String ownerName;

    @SerializedName("lastMessage")
    private String lastMessage;

    @SerializedName("timestamp")
    private String timestamp;

    public Conversation(int ownerId, String ownerName, String lastMessage, String timestamp) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public int getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getLastMessage() { return lastMessage; }
    public String getTimestamp() { return timestamp; }
}
