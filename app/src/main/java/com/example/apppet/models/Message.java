package com.example.apppet.models;

public class Message {
    private int id;
    private int sender_id;
    private int receiver_id;
    private String sender_name; // NUEVO
    private String message_text;
    private String timestamp;

    public Message() { }

    public Message(int sender_id, int receiver_id, String message_text, String timestamp) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message_text = message_text;
        this.timestamp = timestamp;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSender_id() { return sender_id; }
    public void setSender_id(int sender_id) { this.sender_id = sender_id; }

    public int getReceiver_id() { return receiver_id; }
    public void setReceiver_id(int receiver_id) { this.receiver_id = receiver_id; }

    public String getSender_name() { return sender_name; }
    public void setSender_name(String sender_name) { this.sender_name = sender_name; }

    public String getMessage_text() { return message_text; }
    public void setMessage_text(String message_text) { this.message_text = message_text; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}


